import { http } from './http'

export interface Comment {
  id?: number
  postId: number
  userId: number
  username?: string
  nickname?: string
  avatar?: string
  content: string
  image?: string
  parentId?: number
  status?: number
  createTime?: string
}

export interface Post {
  id?: number
  userId: number
  username?: string
  nickname?: string
  avatar?: string
  title: string
  content: string
  images?: string // JSON string
  location?: string
  latitude?: number
  longitude?: number
  status?: number
  createTime?: string
  updateTime?: string
  comments?: Comment[]
}

export async function getPostList() {
  const res = await http.get('/api/community/posts')
  return res.data as { code: number; message: string; data: Post[] }
}

export async function createPost(data: Post) {
  const res = await http.post('/api/community/posts', data)
  return res.data as { code: number; message: string; data: boolean }
}

export async function createComment(data: Comment) {
  const res = await http.post('/api/community/comments', data)
  return res.data as { code: number; message: string; data: boolean }
}

export async function getAdminPostList() {
  const res = await http.get('/api/admin/posts')
  return res.data as { code: number; message: string; data: Post[] }
}

export async function deletePost(id: number) {
  const res = await http.delete(`/api/community/posts/${id}`)
  return res.data as { code: number; message: string; data: boolean }
}

export async function deleteComment(id: number) {
  const res = await http.delete(`/api/community/comments/${id}`)
  return res.data as { code: number; message: string; data: boolean }
}

export async function setPostStatus(id: number, status: number) {
  const res = await http.put(`/api/community/posts/${id}/status`, { status })
  return res.data as { code: number; message: string; data: boolean }
}

export async function setCommentStatus(id: number, status: number) {
  const res = await http.put(`/api/community/comments/${id}/status`, { status })
  return res.data as { code: number; message: string; data: boolean }
}
