import UploadIcon from '~/assets/icons/line-md--file-upload-filled.svg?raw'
import { uploadFiles } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'

import '~/assets/css/components/upload-area.css'

export function UploadArea(originalElement) {
  const container = document.createElement('div')
  container.id = originalElement.id ?? ''
  container.className = originalElement.className ?? ''
  container.classList.add('upload-area-container')
  container.innerHTML = /*html*/ `
    <div class="upload-area" >
      <input type="file" hidden multiple data-role="input" />
      <span class="upload-icon">${UploadIcon}</span>
      <p class="upload-text upload-text--default">Перетащите файлы или нажмите, чтобы выбрать</p>
      <p class="upload-text upload-text--dragover">Отпустите файлы, чтобы загрузить</p>
      <p class="upload-text upload-text--uploading">Дождитесь загрузки файлов</p>
    </div>
  `

  const uploadArea = container.querySelector('.upload-area')
  const uploadAreaInput = uploadArea.querySelector('[data-role="input"]')

  container.addEventListener('click', () => {
    if (filesState.uploading) return
    uploadAreaInput.click()
  })

  container.addEventListener('dragover', (event) => {
    event.preventDefault()
    uploadArea.classList.add('upload-area--dragover')
  })
  container.addEventListener('dragleave', () =>
    uploadArea.classList.remove('upload-area--dragover'),
  )
  container.addEventListener('drop', (event) => {
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
      filesState.files = (filesState.files ?? []).concat(uploadedFiles)
    } catch (error) {
      console.error(error)
      alert(`Ошибка при загрузке: ${error.response.data.title}`)
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

  return container
}
