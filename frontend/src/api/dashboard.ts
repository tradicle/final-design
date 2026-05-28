import { http } from './http'

export interface DashboardSummary {
  totalRescueCount: number
  adoptionSuccessCount: number
  activeAnimals: number
  totalProfiles: number
  monthlyPublicBudget: string
}

export interface WeeklyUpdate {
  title: string
  desc: string
}

export interface TransparencyRow {
  month: string
  income: string
  expense: string
  note: string
}

export interface DashboardChartItem {
  label: string
  value: number
}

export interface DashboardCharts {
  animalCategoryStats: DashboardChartItem[]
  animalStatusStats: DashboardChartItem[]
  adoptionStatusStats: DashboardChartItem[]
  donationClaimStatusStats: DashboardChartItem[]
  overviewStats: DashboardChartItem[]
}

export async function getDashboardSummary() {
  const { data } = await http.get('/api/dashboard/summary')
  return data as { code: number; message: string; data: DashboardSummary }
}

export async function getWeeklyUpdates() {
  const { data } = await http.get('/api/dashboard/weekly-updates')
  return data as { code: number; message: string; data: WeeklyUpdate[] }
}

export async function getTransparencyRows() {
  const { data } = await http.get('/api/dashboard/transparency')
  return data as { code: number; message: string; data: TransparencyRow[] }
}

export async function getDashboardCharts() {
  const { data } = await http.get('/api/dashboard/charts')
  return data as { code: number; message: string; data: DashboardCharts }
}
