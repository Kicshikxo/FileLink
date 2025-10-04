export function formatFileSize(bytes, decimals = 2) {
  if (bytes === 0) return '0 Байт'

  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Байт', 'КБ', 'МБ', 'ГБ', 'ТБ', 'ПБ', 'ЭБ', 'ЗБ', 'ЙБ']

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const size = bytes / Math.pow(k, i)
  return `${size.toFixed(dm)} ${sizes[i]}`
}

export function formatDate(date) {
  return new Date(date).toLocaleString('ru-RU')
}
