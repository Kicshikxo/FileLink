import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'
import { deleteFile } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'
import { formatDate, formatFileSize } from '~/assets/js/utils'

import '~/assets/css/components/uploaded-files.css'

export function UploadedFiles(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('uploaded-files')
  component.innerHTML = /*html*/ `
    <span class="uploaded-files__title">Ваши загруженные файлы</span>
    <span class="uploaded-files__loader">${LoadingIcon}</span>
    <span class="uploaded-files__empty">Файлы не найдены<br>Вы можете загрузить свои файлы <a href="/">здесь</a></span>
    <div class="uploaded-files__list"></div>
  `

  const containerLoader = component.querySelector('.uploaded-files__loader')
  const containerEmpty = component.querySelector('.uploaded-files__empty')
  const filesList = component.querySelector('.uploaded-files__list')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'files') {
      containerLoader.style.display = value ? 'none' : 'block'
      containerEmpty.style.display = value.length ? 'none' : 'block'
      filesList.innerHTML = ''

      for (let file of value) {
        const fileItem = document.createElement('div')
        fileItem.classList.add('uploaded-files__list-item')
        fileItem.innerHTML = /*html*/ `
          <span class="uploaded-files__list-item__name">
            <a href="/information?fileId=${file.fileId}?days=7">${file.fileName}</a> <span class="uploaded-files__list-item__size">(${formatFileSize(file.fileSize)})</span>
          </span>
          <div class="uploaded-files__list-item__actions">
            ${
              file.expiredAt
                ? /*html*/ `
                <div class="uploaded-files__list-item__expired">Файл устарел ${formatDate(file.expiredAt)}</div>
                `
                : /*html*/ `
                <a
                  href="${file.fileShortId ? `${window.location.origin}/id/${file.fileShortId}` : `${window.location.origin}/api/files/download/${file.fileId}`}"
                  class="copy-file-link"
                >
                  <button class="app-button app-button--primary app-button--small copy-file-link-button">Ссылка</button>
                </a>
                <a href="/api/files/download/${file.fileId}" download>
                  <button class="app-button app-button--success app-button--small">Скачать</button>
                </a>
                `
            }
            <a href="/api/files/delete/${file.fileId}" class="delete-file">
              <button class="app-button app-button--danger app-button--small delete-file-button">Удалить</button>
            </a>
          </div>
        `

        const copyFileLink = fileItem.querySelector('.copy-file-link')
        const copyFileLinkButton = fileItem.querySelector('.copy-file-link-button')
        const deleteFileLink = fileItem.querySelector('.delete-file')
        const deleteFileButton = fileItem.querySelector('.delete-file-button')

        copyFileLink?.addEventListener('click', (event) => {
          event.preventDefault()
        })
        copyFileLinkButton?.addEventListener('click', async () => {
          try {
            await navigator.clipboard.writeText(
              file.fileShortId
                ? `${window.location.origin}/id/${file.fileShortId}`
                : `${window.location.origin}/api/files/download/${file.fileId}`,
            )
            alert(`Ссылка на файл "${file.fileName}" скопирована`)
          } catch (error) {
            console.error(error)
          }
        })

        deleteFileLink?.addEventListener('click', (event) => {
          event.preventDefault()
        })
        deleteFileButton?.addEventListener('click', async (event) => {
          event.preventDefault()

          deleteFileButton.disabled = true
          try {
            const success = await deleteFile(file.fileId)
            if (success) {
              filesState.files = (filesState.files ?? []).filter(
                ({ fileId }) => fileId !== file.fileId,
              )
            }
          } catch (error) {
            console.error(error)
            alert(`Ошибка при удалении файла: ${error.response.data.title}`)
          } finally {
            deleteFileButton.disabled = false
          }
        })

        filesList.appendChild(fileItem)
      }
    }
  })

  return component
}
