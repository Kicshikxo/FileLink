import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'
import { checkAuth } from '~/assets/js/api/auth'
import { authState } from '~/assets/js/state/auth'

const authPages = ['/login', '/register']

function showPageLoader() {
  const appMain = document.querySelector('.app-main')
  if (appMain) {
    const appPageLoader = document.createElement('div')
    appPageLoader.classList.add('app-page-loader')
    appPageLoader.innerHTML = /*html*/ `
      <div class="app-page-loader__icon">${LoadingIcon}</div>
    `

    appMain.appendChild(appPageLoader)
  }
}

function hidePageLoader() {
  const appMain = document.querySelector('.app-main')
  if (appMain) {
    const appPageLoader = appMain.querySelector('.app-page-loader')
    if (appPageLoader) {
      appPageLoader.remove()
    }
  }
}

async function authMiddleware() {
  const pathname = window.location.pathname

  try {
    const success = await checkAuth()

    if (success) {
      if (authPages.includes(pathname)) {
        window.location.href = '/'
      }
    }

    authState.isAuth = true
  } catch (error) {
    console.error(error)

    if (error?.response?.status === 401) {
      authState.isAuth = false
    }
    else if (error?.response?.status === 500) {
      alert('Ошибка сервера')
    } else {
      alert('Ошибка авторизации')
    }
  }

  if (authState.isAuth && !authPages.includes(pathname)) {
    hidePageLoader()
  } else if (!authState.isAuth && authPages.includes(pathname)) {
    hidePageLoader()
  } else if (!authState.isAuth) {
    window.location.href = '/login'
  }
}

showPageLoader()
authMiddleware()
