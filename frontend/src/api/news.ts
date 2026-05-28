import { http } from './http'

export interface NewsItem {
  id?: number
  title: string
  summary?: string
  content?: string
  coverImage: string
  publishTime: string
}

export async function getNewsList() {
  const { data } = await http.get('/api/news')
  return data as { code: number; message: string; data: NewsItem[] }
}

export async function createNews(payload: NewsItem) {
  const { data } = await http.post('/api/news', payload)
  return data as { code: number; message: string; data: boolean }
}

export async function updateNews(id: number, payload: NewsItem) {
  const { data } = await http.put(`/api/news/${id}`, payload)
  return data as { code: number; message: string; data: boolean }
}

export async function deleteNews(id: number) {
  const { data } = await http.delete(`/api/news/${id}`)
  return data as { code: number; message: string; data: boolean }
}
