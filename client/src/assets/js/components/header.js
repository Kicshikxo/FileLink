import { logout } from '~/assets/js/api/auth'

import '~/assets/css/components/progress-bar.css'

export function Header(originalElement) {
  const container = document.createElement('header')
  container.id = originalElement.id ?? ''
  container.className = originalElement.className ?? ''
  container.classList.add('app-header')
  container.innerHTML = /*html*/ `
    <a href="/">
      <button class="app-button app-button--text" style="font-size: 1.25rem">
        FileLink
      </button>
    </a>
    <a href="/dashboard" style="margin-right: auto">
      <button class="app-button app-button--text">Мои файлы</button>
    </a>
    <button class="app-button app-button--text app-logout-button">Выйти</button>
  `

  const logoutButton = container.querySelector('.app-logout-button')

  document.addEventListener('authStateChange', async (event) => {
    const { key, value } = event.detail
    if (key === 'isAuth') {
      if (value && logoutButton) {
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
  })

  return container
}
