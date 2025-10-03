import axios from 'axios'

import '~/assets/css/pages/index.css'

const uploadArea = document.querySelector('#upload-area')
const fileUploadInput = document.querySelector('#file-upload-input')

const uploadProgressContainer = document.querySelector('#upload-progress-container')
const progressPercent = document.querySelector('#progress-percent')
const progressBar = document.querySelector('#progress-bar')

const uploadedFilesContainer = document.querySelector('#uploaded-files-container')
const uploadedFilesList = document.querySelector('#uploaded-files-list')

uploadArea.addEventListener('click', () => fileUploadInput.click())

uploadArea.addEventListener('dragover', (event) => {
  event.preventDefault()
  uploadArea.classList.add('file-upload-container__upload-area--dragover')
})
uploadArea.addEventListener('dragleave', () => {
  uploadArea.classList.remove('file-upload-container__upload-area--dragover')
})
uploadArea.addEventListener('drop', (event) => {
  event.preventDefault()
  uploadArea.classList.remove('file-upload-container__upload-area--dragover')
  const files = event.dataTransfer.files
  uploadFiles(files)
})

fileUploadInput.addEventListener('change', (event) => {
  const files = event.target.files
  uploadFiles(files)
})

async function uploadFiles(files) {
  if (!files.length) return

  const formData = new FormData()
  for (let file of files) {
    formData.append('files', file)
  }

  uploadProgressContainer.style.display = 'flex'
  progressPercent.textContent = 0
  progressBar.style.width = `${0}%`

  try {
    const response = await axios.post('/api/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        progressPercent.textContent = percentCompleted
        progressBar.style.width = `${percentCompleted}%`
      },
    })

    uploadedFilesContainer.style.display = 'flex'

    for (let file of response.data) {
      const fileItem = document.createElement('div')
      fileItem.classList.add('uploaded-files-container__list-item')

      const fileItemTitle = document.createElement('span')
      fileItemTitle.textContent = `${file.fileName} (${file.fileSize} байт)`

      const fileItemLink = document.createElement('a')
      fileItemLink.textContent = 'Скачать'
      fileItemLink.href = `/api/files/download/${file.fileId}`
      fileItemLink.target = '_blank'

      fileItem.appendChild(fileItemTitle)
      fileItem.appendChild(fileItemLink)

      uploadedFilesList.appendChild(fileItem)
    }
  } catch (error) {
    alert(error.response.data.title)
  } finally {
    fileUploadInput.value = null
    uploadProgressContainer.style.display = 'none'
  }
}
