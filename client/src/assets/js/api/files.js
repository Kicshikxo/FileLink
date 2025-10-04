import axios from 'axios'

export async function getUploadedFiles() {
  const response = await axios.get('/api/files/list')

  return response.data
}

export async function getFileStatistics(fileId, days = 7) {
  const response = await axios.get(`/api/files/statistics/${fileId}`, {
    params: { days },
  })

  return response.data
}

export async function deleteFile(fileId) {
  const response = await axios.delete(`/api/files/delete/${fileId}`)

  return response.status === 200
}

export async function uploadFiles(files, onProgress) {
  if (!files.length) return

  const formData = new FormData()
  for (let file of files) {
    formData.append('files', file)
  }

  const response = await axios.post('/api/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (event) => {
      const percent = Math.round((event.loaded * 100) / event.total)
      onProgress(percent)
    },
  })

  return response.data
}
