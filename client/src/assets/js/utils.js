export function pluralize(count, one, few, many) {
  const mod10 = count % 10
  const mod100 = count % 100
  if (mod100 >= 11 && mod100 <= 20) return many
  if (mod10 === 1) return one
  if (mod10 >= 2 && mod10 <= 4) return few
  return many
}

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

export function getShareLink(file) {
  const shortLinkBase = import.meta.env.VITE_SHARE_LINK_BASE
  if (shortLinkBase && file?.fileShortId) {
    return new URL(file.fileShortId, shortLinkBase).toString()
  }
  return file?.fileShortId
    ? `${window.location.origin}/id/${file.fileShortId}`
    : `${window.location.origin}/api/files/download/${file.fileId}`
}
