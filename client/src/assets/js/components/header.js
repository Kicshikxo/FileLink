import LogoIcon from '~/assets/icons/line-md--cloud-alt-upload-filled.svg?raw'
import DashboardIcon from '~/assets/icons/line-md--file-search-filled.svg?raw'
import LogoutIcon from '~/assets/icons/line-md--logout.svg?raw'
import { logout } from '~/assets/js/api/auth'

import '~/assets/css/components/progress-bar.css'

export function Header(originalElement) {
  const component = document.createElement('header')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('app-header')
  component.innerHTML = /*html*/ `
    <div class="app-header__container">
      <a href="/" >
        <button class="app-button app-button--text app-header__button" style="font-size: 1.25rem">
          <span class="app-header__logo">${LogoIcon}</span>
          <span>FileLink</span>
        </button>
      </a>
      <a href="/dashboard">
        <button class="app-button app-button--text app-header__button">
          <span class="app-header__icon">${DashboardIcon}</span>
          <span>Мои файлы</span>
        </button>
      </a>
    </div>
    <div class="app-header__container">
      <button class="app-button app-button--text app-header__button app-logout-button">
        <span>Выйти</span>
        <span class="app-header__icon">${LogoutIcon}</span>
      </button>
    </div>
  `

  const logoutButton = component.querySelector('.app-logout-button')

  document.addEventListener('authStateChange', async (event) => {
    const { key, value } = event.detail
    if (key === 'isAuth') {
      if (value && logoutButton) {
        logoutButton.style.display = 'flex'
        logoutButton.addEventListener('click', async () => {
          logoutButton.disabled = true
          try {
            const success = await logout()
            if (success) {
              window.location.href = '/login'
            }
          } catch (error) {
            console.error(error)
            alert(`Ошибка при выходе из системы: ${error.response.data.title}`)
          } finally {
            logoutButton.disabled = false
          }
        })
      }
    }
  })

  return component
}
