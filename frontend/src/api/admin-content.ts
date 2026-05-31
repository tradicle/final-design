import { http } from './http'

export interface WeeklyUpdateRow {
  id?: number
  title: string
  description: string
  sortOrder: number
}

export interface TransparencyRowManage {
  id?: number
  month: string
  income: string
  expense: string
  note: string
  sortOrder: number
}

export interface UrgentNeedRow {
  id?: number
  name: string
  gap: string
  updatedAt: string
  sortOrder: number
}

export interface DonationRecordRow {
  id?: number
  date: string
  donor: string
  item: string
  quantity: string
  unit: string
  remark: string
  sortOrder: number
}

export interface DashboardMetricsRow {
  totalRescueCount: string
  adoptionSuccessBase: string
}

export async function listWeeklyUpdates() {
  const { data } = await http.get('/api/admin/content/weekly-updates')
  return data as { code: number; message: string; data: WeeklyUpdateRow[] }
}
export async function createWeeklyUpdate(payload: WeeklyUpdateRow) {
  const { data } = await http.post('/api/admin/content/weekly-updates', payload)
  return data as { code: number; message: string; data: boolean }
}
export async function updateWeeklyUpdate(id: number, payload: WeeklyUpdateRow) {
  const { data } = await http.put(`/api/admin/content/weekly-updates/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}
export async function deleteWeeklyUpdate(id: number) {
  const { data } = await http.delete(`/api/admin/content/weekly-updates/${id}`)
  return data as { code: number; message: string; data: boolean }
}

export async function listTransparency() {
  const { data } = await http.get('/api/admin/content/transparency')
  return data as { code: number; message: string; data: TransparencyRowManage[] }
}
export async function createTransparency(payload: TransparencyRowManage) {
  const { data } = await http.post('/api/admin/content/transparency', payload)
  return data as { code: number; message: string; data: boolean }
}
export async function updateTransparency(id: number, payload: TransparencyRowManage) {
  const { data } = await http.put(`/api/admin/content/transparency/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}
export async function deleteTransparency(id: number) {
  const { data } = await http.delete(`/api/admin/content/transparency/${id}`)
  return data as { code: number; message: string; data: boolean }
}

export async function listUrgentNeeds() {
  const { data } = await http.get('/api/admin/content/urgent-needs')
  return data as { code: number; message: string; data: UrgentNeedRow[] }
}
export async function createUrgentNeed(payload: UrgentNeedRow) {
  const { data } = await http.post('/api/admin/content/urgent-needs', payload)
  return data as { code: number; message: string; data: boolean }
}
export async function updateUrgentNeed(id: number, payload: UrgentNeedRow) {
  const { data } = await http.put(`/api/admin/content/urgent-needs/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}
export async function deleteUrgentNeed(id: number) {
  const { data } = await http.delete(`/api/admin/content/urgent-needs/${id}`)
  return data as { code: number; message: string; data: boolean }
}

export async function listDonationRecordsManage() {
  const { data } = await http.get('/api/admin/content/donation-records')
  return data as { code: number; message: string; data: DonationRecordRow[] }
}
export async function createDonationRecord(payload: DonationRecordRow) {
  const { data } = await http.post('/api/admin/content/donation-records', payload)
  return data as { code: number; message: string; data: boolean }
}
export async function updateDonationRecord(id: number, payload: DonationRecordRow) {
  const { data } = await http.put(`/api/admin/content/donation-records/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}
export async function deleteDonationRecord(id: number) {
  const { data } = await http.delete(`/api/admin/content/donation-records/${id}`)
  return data as { code: number; message: string; data: boolean }
}

export async function getDashboardMetrics() {
  const { data } = await http.get('/api/admin/content/dashboard-metrics')
  return data as { code: number; message: string; data: DashboardMetricsRow }
}

export async function updateDashboardMetrics(payload: DashboardMetricsRow) {
  const { data } = await http.put('/api/admin/content/dashboard-metrics', payload)
  return data as { code: number; message: string; data: boolean }
}
