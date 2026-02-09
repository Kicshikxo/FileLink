export function formatFileSize(bytes, decimals = 2) {
  if (bytes <= 0) return '0 Байт'

  const kilo = 1024
  const precision = decimals < 0 ? 0 : decimals
  const units = ['Байт', 'КБ', 'МБ', 'ГБ', 'ТБ', 'ПБ', 'ЭБ', 'ЗБ', 'ЙБ']

  const unitIndex = Math.floor(Math.log(bytes) / Math.log(kilo))
  const sizeInUnit = bytes / Math.pow(kilo, unitIndex)
  return `${sizeInUnit.toFixed(precision)} ${units[unitIndex]}`
}

export function formatDate(date) {
  return new Date(date).toLocaleDateString('ru-RU')
}

export function formatDateTime(date) {
  return new Date(date).toLocaleString('ru-RU')
}
