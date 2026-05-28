import { http } from './http'

export interface DonationClaimPayload {
  needName: string
  needGap: string
  quantity: string
  contactName: string
  phone: string
  wechat?: string
  pickupDate?: string
  remark?: string
  status?: number
  reviewNote?: string
}

export interface DonationClaimRow extends DonationClaimPayload {
  id: number
  createTime?: string
  updateTime?: string
}

export interface DonationClaimPage {
  records: DonationClaimRow[]
  total: number
}

export async function createDonationClaim(payload: DonationClaimPayload) {
  const { data } = await http.post('/api/donation/claims', payload)
  return data as { code: number; message: string; data: boolean }
}

export async function listDonationClaims(params: { status?: number; keyword?: string; page: number; pageSize: number }) {
  const { data } = await http.get('/api/admin/donation-claims', { params })
  return data as { code: number; message: string; data: DonationClaimPage }
}

export async function reviewDonationClaim(id: number, status: number, reviewNote = '') {
  const { data } = await http.put(`/api/admin/donation-claims/${id}/status`, { status, reviewNote })
  return data as { code: number; message: string; data: boolean }
}

export async function deleteDonationClaim(id: number) {
  const { data } = await http.delete(`/api/admin/donation-claims/${id}`)
  return data as { code: number; message: string; data: boolean }
}
