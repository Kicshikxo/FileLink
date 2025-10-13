import UploadIcon from '~/assets/icons/line-md--file-upload-filled.svg?raw'
import { uploadFiles } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'

import '~/assets/css/components/upload-area.css'

export function UploadArea(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('upload-area__wrapper')
  component.innerHTML = /*html*/ `
    <div class="upload-area">
      <input type="file" hidden multiple data-role="input" />
      <span class="upload-icon">${UploadIcon}</span>
      <p class="upload-text upload-text--default">Перетащите файлы или нажмите, чтобы выбрать</p>
      <p class="upload-text upload-text--dragover">Отпустите файлы, чтобы загрузить</p>
      <p class="upload-text upload-text--uploading">Дождитесь загрузки файлов</p>
    </div>
  `

  const uploadArea = component.querySelector('.upload-area')
  const uploadAreaInput = uploadArea.querySelector('[data-role="input"]')

  component.addEventListener('click', () => {
    if (filesState.uploading) return
    uploadAreaInput.click()
  })

  component.addEventListener('dragover', (event) => {
    event.preventDefault()
    uploadArea.classList.add('upload-area--dragover')
  })
  component.addEventListener('dragleave', () =>
    uploadArea.classList.remove('upload-area--dragover'),
  )
  component.addEventListener('drop', (event) => {
    event.preventDefault()
    uploadArea.classList.remove('upload-area--dragover')

    handleFilesInput(event.dataTransfer.files)
  })
  component.addEventListener('paste', (event) => {
    const items = event.clipboardData.items;
    const files = []
    for (const item of items) {
      if (item.kind === 'file') {
        files.push(item.getAsFile())
      }
    }

    handleFilesInput(files)
  });

  uploadAreaInput.addEventListener('change', (event) => {
    handleFilesInput(event.target.files)
  })

  async function handleFilesInput(files) {
    if (filesState.uploading) return
    if (!files.length) return

    const maxFileSize = 100 * 1024 * 1024 // 100 MB
    for (const file of files) {
      if (file.size > maxFileSize) {
        alert(`Файл "${file.name}" превышает 100 МБ и не может быть загружен.`)
        return
      }
    }

    const maxRequestSize = 1024 * 1024 * 1024 // 1 GB
    const totalSize = Array.from(files).reduce((acc, file) => acc + file.size, 0)
    if (totalSize > maxRequestSize) {
      alert('Суммарный размер выбранных файлов превышает 1 ГБ и не может быть загружен.')
      return
    }

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

  return component
}
