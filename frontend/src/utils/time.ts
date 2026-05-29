export function relativeTime(time: string | undefined): string {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return time.replace('T', ' ')
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const seconds = Math.floor(diff / 1000)
  if (seconds < 60) return '刚刚'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.getFullYear() === now.getFullYear()) {
    if (date.getMonth() === yesterday.getMonth() && date.getDate() === yesterday.getDate()) {
      return `昨天 ${pad(date.getHours())}:${pad(date.getMinutes())}`
    }
    return `${date.getMonth() + 1}-${pad(date.getDate())}`
  }
  return `${date.getFullYear()}-${date.getMonth() + 1}-${pad(date.getDate())}`
}

function pad(n: number): string {
  return n < 10 ? '0' + n : String(n)
}
