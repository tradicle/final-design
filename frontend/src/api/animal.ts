import { http } from './http'

export interface AnimalLocation {
  id: number
  animalId: number
  latitude: number
  longitude: number
  recordTime: string
}

export interface Animal {
  id: number
  animalNo?: string
  name: string
  category: string
  sex?: string
  bodySize?: string
  age: number
  avatar: string
  isSterilized: boolean
  activityScope: string
  status: number
  description: string
  detailContent?: string
  latitude?: number
  longitude?: number
  createTime: string
  updateTime: string
  locations?: AnimalLocation[]
}

export async function helloAnimal() {
  const { data } = await http.get('/api/animals/hello')
  return data as { code: number; message: string; data: string }
}

export async function getAnimalList(params?: { category?: string; sex?: string; bodySize?: string }) {
  const { data } = await http.get('/api/animals', { params })
  return data as { code: number; message: string; data: Animal[] }
}

export async function getAnimalDetail(id: number) {
  const { data } = await http.get(`/api/animals/${id}`)
  return data as { code: number; message: string; data: Animal }
}

export async function getAnimalByNo(animalNo: string) {
  const { data } = await http.get(`/api/animals/no/${animalNo}`)
  return data as { code: number; message: string; data: Animal }
}

export async function createAnimal(animal: Partial<Animal>) {
  const { data } = await http.post('/api/animals', animal)
  return data as { code: number; message: string; data: boolean }
}
