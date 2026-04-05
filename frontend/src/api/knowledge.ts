import { http } from './http'

export interface KnowledgeItem {
  id: number
  title: string
  content: string
  sortOrder: number
}

export async function getKnowledgeList() {
  const { data } = await http.get('/api/knowledge')
  return data as { code: number; message: string; data: KnowledgeItem[] }
}

export async function getKnowledgeDetail(id: number | string) {
  const { data } = await http.get(`/api/knowledge/${id}`)
  return data as { code: number; message: string; data: KnowledgeItem | null }
}
