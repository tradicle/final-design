import { http } from './http'

export interface Comment {
  id?: number
  postId: number
  userId: number
  username?: string
  avatar?: string
  content: string
  image?: string
  parentId?: number
  createTime?: string
}

export interface Post {
  id?: number
  userId: number
  username?: string
  avatar?: string
  title: string
  content: string
  images?: string // JSON string
  location?: string
  latitude?: number
  longitude?: number
  createTime?: string
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
