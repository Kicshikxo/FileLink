import { checkAuth } from '~/assets/js/api/auth'
import { initComponents } from '~/assets/js/components'
import { authState } from '~/assets/js/state/auth'

import '~/assets/css/main.css'
import '~/assets/css/rubik.fontface.css'

async function main() {
  initComponents()

  try {
    const success = await checkAuth()

    if (success) {
      if (window.location.pathname === '/login' || window.location.pathname === '/register') {
        window.location.href = '/'
      }
    }

    authState.isAuth = true
  } catch (error) {
    if (
      error.response.status === 401 &&
      window.location.pathname !== '/login' &&
      window.location.pathname !== '/register'
    ) {
      window.location.href = '/login'

      authState.isAuth = false
    }
    if (error.response.status === 500) {
      alert('Ошибка сервера')
    }
  }
}

main()
