export type Role = 'MANAGER' | 'DISPATCHER' | 'TECHNICIAN' | 'CUSTOMER'
export type Status = 'NEW' | 'ASSIGNED' | 'IN_PROGRESS' | 'ON_HOLD' | 'COMPLETED' | 'CLOSED' | 'CANCELLED'
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export interface LoginResponse { token: string; role: Role; name: string }
export interface WorkOrder {
  id: number; code: string; title: string; description?: string
  priority: Priority; status: Status; slaDueAt: string
  customer: { id: number; name: string }; site: { id: number; name: string; city: string }
  assignee?: { id: number; fullName: string; email: string }
}
export interface Summary { statusCounts: Record<string, number>; overdueWorkOrders: number }
