import axios from 'axios'

export async function getUploadedFiles() {
  const response = await axios.get('/api/files/list')

  return response.data
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
