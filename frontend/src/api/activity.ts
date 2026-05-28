import { http } from './http'

export interface ActivityItem {
  id?: number
  title: string
  summary?: string
  content?: string
  coverImage?: string
  publishTime?: string
}

export async function getActivityList() {
  const { data } = await http.get('/api/activities')
  return data as { code: number; message: string; data: ActivityItem[] }
}

export async function getActivityDetail(id: number) {
  const { data } = await http.get(`/api/activities/${id}`)
  return data as { code: number; message: string; data: ActivityItem }
}

export async function getAdminActivityList() {
  const { data } = await http.get('/api/admin/activities')
  return data as { code: number; message: string; data: ActivityItem[] }
}

export async function createActivity(payload: ActivityItem) {
  const { data } = await http.post('/api/admin/activities', payload)
  return data as { code: number; message: string; data: boolean }
}

export async function updateActivity(id: number, payload: ActivityItem) {
  const { data } = await http.put(`/api/admin/activities/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}

export async function deleteActivity(id: number) {
  const { data } = await http.delete(`/api/admin/activities/${id}`)
  return data as { code: number; message: string; data: boolean }
}

export async function importActivityWord(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await http.post('/api/admin/activities/import-word', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data as { code: number; message: string; data: { title: string; summary: string; content: string } }
}
