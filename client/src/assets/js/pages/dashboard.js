import { getUploadedFiles } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'

import '~/assets/css/pages/dashboard.css'

document.addEventListener('authStateChange', async (event) => {
  const { key, value } = event.detail
  if (key === 'isAuth') {
    if (value) {
      try {
        const uploadedFiles = await getUploadedFiles()
        filesState.files = filesState.files.concat(uploadedFiles)
      } catch (event) {
        console.error(event)
        alert(`Ошибка получения файлов: ${event.response.data.title}`)
      }
    }
  }
})
