import axios from 'axios'

import '~/assets/css/pages/dashboard.css'

const uploadedFilesList = document.querySelector('#uploaded-files-list')

async function getUploadedFiles() {
  try {
    const response = await axios.get('/api/files/list')

    uploadedFilesList.innerHTML = ''

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
    console.error(error)
  }
}

getUploadedFiles()
