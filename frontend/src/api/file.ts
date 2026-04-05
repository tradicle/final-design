import { http } from './http'

export async function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const res = await http.post('/api/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return res.data as { code: number; message: string; data: string }
}
