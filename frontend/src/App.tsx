import { useEffect, useMemo, useState } from 'react'
import { api } from './api'
import type { LoginResponse, Role, Status, WorkOrder, Summary } from './types'

const roles: Role[] = ['MANAGER','DISPATCHER','TECHNICIAN','CUSTOMER']

function App() {
  const [session, setSession] = useState<LoginResponse | null>(() => {
    const token = localStorage.getItem('keystone_token')
    const raw = localStorage.getItem('keystone_session')
    return token && raw ? JSON.parse(raw) : null
  })
  const [error, setError] = useState('')

  function login(result: LoginResponse) {
    localStorage.setItem('keystone_token', result.token)
    localStorage.setItem('keystone_session', JSON.stringify(result))
    setSession(result); setError('')
  }

  function logout() {
    localStorage.clear(); setSession(null)
  }

  if (!session) return <Login onLogin={login} onError={setError} error={error} />
  return <Dashboard session={session} logout={logout} onError={setError} error={error} />
}

function Login({ onLogin, onError, error }: {onLogin:(r:LoginResponse)=>void; onError:(s:string)=>void; error:string}) {
  const [email, setEmail] = useState('manager@keystone.local')
  const [password, setPassword] = useState('Password123!')
  const [loading, setLoading] = useState(false)

  async function submit(e: React.FormEvent) {
    e.preventDefault(); setLoading(true)
    try {
      const result = await api<LoginResponse>('/api/auth/login', {method:'POST', body:JSON.stringify({email,password})})
      onLogin(result)
    } catch (e) { onError(e instanceof Error ? e.message : 'Login failed') }
    finally { setLoading(false) }
  }

  return <main className="auth-shell">
    <section className="login-card">
      <div className="brand-mark">K</div>
      <p className="eyebrow">FIELD SERVICE PLATFORM</p>
      <h1>KEYSTONE</h1>
      <p className="muted">One workspace for dispatch, field work, SLAs and customer visibility.</p>
      <form onSubmit={submit}>
        <label>Email<input value={email} onChange={e=>setEmail(e.target.value)} type="email" /></label>
        <label>Password<input value={password} onChange={e=>setPassword(e.target.value)} type="password" /></label>
        {error && <div className="error">{error}</div>}
        <button disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
      </form>
      <div className="seed-box">
        <strong>Demo accounts</strong>
        <span>manager@keystone.local</span>
        <span>dispatcher@keystone.local</span>
        <span>technician@keystone.local</span>
        <span>customer@keystone.local</span>
        <small>Password: Password123!</small>
      </div>
    </section>
  </main>
}

function Dashboard({ session, logout, onError, error }: {session:LoginResponse; logout:()=>void; onError:(s:string)=>void; error:string}) {
  const [orders, setOrders] = useState<WorkOrder[]>([])
  const [summary, setSummary] = useState<Summary | null>(null)
  const [filter, setFilter] = useState('')
  const [selected, setSelected] = useState<WorkOrder | null>(null)
  const [busy, setBusy] = useState(false)

  async function refresh() {
    try {
      setOrders(await api<WorkOrder[]>('/api/work-orders'))
      if (session.role === 'MANAGER' || session.role === 'DISPATCHER')
        setSummary(await api<Summary>('/api/reports/summary'))
    } catch (e) { onError(e instanceof Error ? e.message : 'Unable to load data') }
  }

  useEffect(() => { refresh() }, [session.role])

  const visible = useMemo(() => orders.filter(o =>
    `${o.code} ${o.title} ${o.status} ${o.priority}`.toLowerCase().includes(filter.toLowerCase())
  ), [orders, filter])

  async function changeStatus(id:number, status:Status) {
    setBusy(true)
    try { await api(`/api/work-orders/${id}/status`, {method:'POST',body:JSON.stringify({status})}); await refresh() }
    catch(e){ onError(e instanceof Error ? e.message : 'Status update failed') }
    finally { setBusy(false) }
  }

  return <div className="app-shell">
    <header className="topbar">
      <div><span className="brand">KEYSTONE</span><span className="role-pill">{session.role}</span></div>
      <div className="top-actions"><span>{session.name}</span><button className="ghost" onClick={logout}>Log out</button></div>
    </header>

    <main className="content">
      <div className="page-heading">
        <div><p className="eyebrow">OPERATIONS</p><h2>{session.role === 'CUSTOMER' ? 'My requests' : 'Work orders'}</h2><p className="muted">Track work from request to close-out.</p></div>
        <button onClick={refresh}>Refresh</button>
      </div>

      {error && <div className="error banner">{error}</div>}

      {summary && <section className="metrics">
        <Metric title="New" value={summary.statusCounts.NEW || 0} />
        <Metric title="In progress" value={summary.statusCounts.IN_PROGRESS || 0} />
        <Metric title="Completed" value={summary.statusCounts.COMPLETED || 0} />
        <Metric title="Overdue" value={summary.overdueWorkOrders} />
      </section>}

      <section className="panel">
        <div className="toolbar">
          <input placeholder="Search work orders…" value={filter} onChange={e=>setFilter(e.target.value)} />
          <span className="muted">{visible.length} result(s)</span>
        </div>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Code</th><th>Title</th><th>Priority</th><th>Status</th><th>Site</th><th>SLA</th><th></th></tr></thead>
            <tbody>
              {visible.map(o => <tr key={o.id}>
                <td><strong>{o.code}</strong></td><td>{o.title}</td>
                <td><span className={`priority ${o.priority.toLowerCase()}`}>{o.priority}</span></td>
                <td><span className={`status ${o.status.toLowerCase()}`}>{o.status.replace('_',' ')}</span></td>
                <td>{o.site.name}</td><td>{new Date(o.slaDueAt).toLocaleString()}</td>
                <td><button className="small" onClick={()=>setSelected(o)}>Open</button></td>
              </tr>)}
            </tbody>
          </table>
        </div>
      </section>

      {selected && <WorkOrderDrawer order={selected} role={session.role} busy={busy} onClose={()=>setSelected(null)} onStatus={changeStatus} />}
    </main>
  </div>
}

function Metric({title,value}:{title:string;value:number}) {
  return <div className="metric"><span>{title}</span><strong>{value}</strong></div>
}

function WorkOrderDrawer({order,role,busy,onClose,onStatus}:{order:WorkOrder;role:Role;busy:boolean;onClose:()=>void;onStatus:(id:number,s:Status)=>void}) {
  const transitions: Partial<Record<Role, Status[]>> = {
    MANAGER: ['ASSIGNED','IN_PROGRESS','ON_HOLD','COMPLETED','CLOSED','CANCELLED'],
    DISPATCHER: ['ASSIGNED','CANCELLED'],
    TECHNICIAN: ['IN_PROGRESS','ON_HOLD','COMPLETED'],
    CUSTOMER: []
  }
  const options = transitions[role] || []

  return <div className="drawer-backdrop" onClick={onClose}>
    <aside className="drawer" onClick={e=>e.stopPropagation()}>
      <button className="close" onClick={onClose}>×</button>
      <p className="eyebrow">{order.code}</p><h3>{order.title}</h3>
      <div className="detail-grid">
        <span>Status</span><strong>{order.status}</strong>
        <span>Priority</span><strong>{order.priority}</strong>
        <span>Customer</span><strong>{order.customer.name}</strong>
        <span>Site</span><strong>{order.site.name}, {order.site.city}</strong>
        <span>Technician</span><strong>{order.assignee?.fullName || 'Unassigned'}</strong>
        <span>SLA</span><strong>{new Date(order.slaDueAt).toLocaleString()}</strong>
      </div>
      <p>{order.description || 'No description provided.'}</p>
      {options.length > 0 && <div className="status-actions">
        <p className="eyebrow">Allowed actions</p>
        {options.map(s=><button disabled={busy} key={s} onClick={()=>onStatus(order.id,s)}>{s.replace('_',' ')}</button>)}
      </div>}
      <div className="note-box">Lifecycle rules are enforced by the Spring service layer, not only by this UI.</div>
    </aside>
  </div>
}

export default App
