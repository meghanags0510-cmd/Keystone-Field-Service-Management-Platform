const API = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('keystone_token')
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const response = await fetch(`${API}${path}`, { ...options, headers })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.message || 'Request failed')
  return body as T
}
