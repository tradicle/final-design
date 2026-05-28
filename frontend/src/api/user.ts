import { http } from './http'

export interface User {
  id: number
  username: string
  nickname?: string
  email?: string
  role: string
  avatar: string
  createTime: string
  updateTime?: string
}

export async function login(data: any) {
  const res = await http.post('/api/user/login', data)
  return res.data as { code: number; message: string; data: User }
}

export async function register(data: any) {
  const res = await http.post('/api/user/register', data)
  return res.data as { code: number; message: string; data: User }
}

export interface DefaultAvatar {
  id: number
  name: string
  imageData: string
  sortOrder: number
}

export async function getProfile() {
  const res = await http.get('/api/user/profile')
  return res.data as { code: number; message: string; data: User }
}

export async function updateProfile(payload: { nickname: string; email: string; avatar: string }) {
  const res = await http.put('/api/user/profile', payload)
  return res.data as { code: number; message: string; data: User }
}

export async function changePassword(payload: { oldPassword: string; newPassword: string; confirmPassword: string }) {
  const res = await http.put('/api/user/password', payload)
  return res.data as { code: number; message: string; data: null }
}

export async function getDefaultAvatars() {
  const res = await http.get('/api/user/default-avatars')
  return res.data as { code: number; message: string; data: DefaultAvatar[] }
}
