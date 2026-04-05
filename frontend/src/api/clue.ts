import { http } from './http'

export interface Clue {
  id?: number
  title: string
  content: string
  location: string
  contact: string
  image: string
  createTime?: string
}

export async function submitClue(data: Clue) {
  const res = await http.post('/api/clue', data)
  return res.data as { code: number; message: string; data: boolean }
}

export async function getClueList() {
  const res = await http.get('/api/clue')
  return res.data as { code: number; message: string; data: Clue[] }
}
