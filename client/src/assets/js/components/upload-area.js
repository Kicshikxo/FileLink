import UploadIcon from '~/assets/icons/line-md--file-upload-filled.svg?raw'
import { uploadFiles } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'

import '~/assets/css/components/upload-area.css'

export function UploadArea(originalElement) {
  const uploadAreaContainer = document.createElement('div')
  uploadAreaContainer.id = originalElement.id ?? ''
  uploadAreaContainer.className = originalElement.className ?? ''
  uploadAreaContainer.classList.add('upload-area-container')
  uploadAreaContainer.innerHTML = /*html*/ `
    <div class="upload-area" >
      <input type="file" hidden multiple data-role="input" />
      <span class="upload-icon">${UploadIcon}</span>
      <p class="upload-text upload-text--default">Перетащите файлы или нажмите, чтобы выбрать</p>
      <p class="upload-text upload-text--dragover">Отпустите файлы, чтобы загрузить</p>
      <p class="upload-text upload-text--uploading">Дождитесь загрузки файлов</p>
    </div>
  `

  const uploadArea = uploadAreaContainer.querySelector('.upload-area')
  const uploadAreaInput = uploadArea.querySelector('[data-role="input"]')

  uploadAreaContainer.addEventListener('click', () => {
    if (filesState.uploading) return
    uploadAreaInput.click()
  })

  uploadAreaContainer.addEventListener('dragover', (event) => {
    event.preventDefault()
    uploadArea.classList.add('upload-area--dragover')
  })
  uploadAreaContainer.addEventListener('dragleave', () =>
    uploadArea.classList.remove('upload-area--dragover'),
  )
  uploadAreaContainer.addEventListener('drop', (event) => {
    event.preventDefault()
    uploadArea.classList.remove('upload-area--dragover')
    if (filesState.uploading) return
    handleFilesInput(event.dataTransfer.files)
  })
  uploadAreaInput.addEventListener('change', (event) => {
    if (filesState.uploading) return
    handleFilesInput(event.target.files)
  })

  async function handleFilesInput(files) {
    if (!files.length) return
    filesState.uploading = true
    filesState.progress = 0

    try {
      const uploadedFiles = await uploadFiles(files, (percent) => (filesState.progress = percent))
      filesState.files = filesState.files.concat(uploadedFiles)
    } catch (event) {
      console.error(event)
      alert(`Ошибка при загрузке: ${event.response.data.title}`)
    } finally {
      filesState.uploading = false
      uploadAreaInput.value = ''
    }
  }

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'uploading') {
      uploadArea.classList.toggle('upload-area--uploading', value)
    }
  })

  return uploadAreaContainer
}
