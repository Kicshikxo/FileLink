import { checkAuth, logout } from '~/assets/js/api/auth'
import { initComponents } from '~/assets/js/components'
import { authState } from '~/assets/js/state/auth'

import '~/assets/css/main.css'
import '~/assets/css/rubik.fontface.css'

async function main() {
  initComponents()

  try {
    const success = await checkAuth()

    if (success.status === 200) {
      if (window.location.pathname === '/login') {
        window.location.href = '/'
      }
    }

    authState.isAuth = true
  } catch (error) {
    if (error.response.status === 401 && window.location.pathname !== '/login') {
      window.location.href = '/login'

      authState.isAuth = false
    }
    if (error.response.status === 500) {
      alert('Ошибка сервера')
    }
  }
}

document.addEventListener('authStateChange', async (event) => {
  const { key, value } = event.detail
  if (key === 'isAuth') {
    if (value) {
      const logoutButton = document.querySelector('.app-logout-button')
      if (logoutButton) {
        logoutButton.style.display = 'block'
        logoutButton.addEventListener('click', async () => {
          try {
            const success = await logout()
            if (success) {
              window.location.href = '/login'
            }
          } catch (error) {
            console.error(error)
            alert(`Ошибка при выходе из системы: ${error.response.data.title}`)
          }
        })
      }
    }
  }
})

main()
