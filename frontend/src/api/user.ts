import { http } from './http'

export interface User {
  id: number
  username: string
  role: string
  avatar: string
  createTime: string
}

export async function login(data: any) {
  const res = await http.post('/api/user/login', data)
  return res.data as { code: number; message: string; data: User }
}

export async function register(data: any) {
  const res = await http.post('/api/user/register', data)
  return res.data as { code: number; message: string; data: User }
}
