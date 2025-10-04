import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'

import '~/assets/css/components/uploaded-files.css'

export function UploadedFiles(originalElement) {
  const uploadedFilesContainer = document.createElement('div')
  uploadedFilesContainer.id = originalElement.id ?? ''
  uploadedFilesContainer.className = originalElement.className ?? ''
  uploadedFilesContainer.classList.add('uploaded-files-container')
  uploadedFilesContainer.innerHTML = /*html*/ `
    <span class="uploaded-files__title">Загруженные файлы</span>
    <span class="uploaded-files__loader">${LoadingIcon}</span>
    <div class="uploaded-files__list"></div>
  `

  const uploadedFilesLoader = uploadedFilesContainer.querySelector('.uploaded-files__loader')
  const uploadedFilesList = uploadedFilesContainer.querySelector('.uploaded-files__list')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'files') {
      uploadedFilesLoader.style.display = value.length ? 'none' : 'block'
      uploadedFilesList.innerHTML = ''

      for (let file of value) {
        const fileItem = document.createElement('div')
        fileItem.classList.add('uploaded-files__list-item')

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
    }
  })

  return uploadedFilesContainer
}
