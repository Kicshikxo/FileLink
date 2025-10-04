import { getFileStatistics } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'

import '~/assets/css/pages/statistics.css'

document.addEventListener('authStateChange', async (event) => {
  const { key, value } = event.detail
  if (key === 'isAuth') {
    if (!value) return

    try {
      const urlParams = new URLSearchParams(window.location.search)
      const fileStatistics = await getFileStatistics(urlParams.get('fileId'))

      filesState.statistics = fileStatistics
    } catch (error) {
      filesState.statistics = null
      console.error(error)
    }
  }
})
