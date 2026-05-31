import { http } from './http'

export interface DonationRecord {
  date: string
  donor: string
  item: string
  quantity: string
  unit: string
  remark: string
}

export interface UrgentNeed {
  id: number
  name: string
  gap: string
  updatedAt: string
}

export async function getDonationRecords() {
  const { data } = await http.get('/api/donation/records')
  return data as { code: number; message: string; data: DonationRecord[] }
}

export async function getUrgentNeeds() {
  const { data } = await http.get('/api/donation/urgent')
  return data as { code: number; message: string; data: UrgentNeed[] }
}
