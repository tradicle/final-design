import { http } from './http'

export interface AdoptionApplicationPayload {
  animalId?: number
  userId?: number
  applicantName: string
  age: number
  job: string
  income: string
  address: string
  phone: string
  wechat: string
  housing: string
  experience: string
  familyMembers: string
  reason: string
  status?: number
  reviewNote?: string
}

export interface AdoptionApplicationRow extends AdoptionApplicationPayload {
  id: number
  createTime?: string
  updateTime?: string
}

export interface AdoptionApplicationPage {
  records: AdoptionApplicationRow[]
  total: number
}

export async function createAdoptionApplication(payload: AdoptionApplicationPayload) {
  const { data } = await http.post('/api/adoption-applications', payload)
  return data as { code: number; message: string; data: boolean }
}

export async function listAdoptionApplications(params: { status?: number; keyword?: string; page: number; pageSize: number }) {
  const { data } = await http.get('/api/admin/adoption-applications', { params })
  return data as { code: number; message: string; data: AdoptionApplicationPage }
}

export async function reviewAdoptionApplication(id: number, status: number, reviewNote = '') {
  const { data } = await http.put(`/api/admin/adoption-applications/${id}/status`, { status, reviewNote })
  return data as { code: number; message: string; data: boolean }
}

export async function deleteAdoptionApplication(id: number) {
  const { data } = await http.delete(`/api/admin/adoption-applications/${id}`)
  return data as { code: number; message: string; data: boolean }
}
