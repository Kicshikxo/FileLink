import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'
import { authState } from '~/assets/js/state/auth'

async function showPageLoader() {
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

async function hidePageLoader() {
  const appMain = document.querySelector('.app-main')
  if (appMain) {
    const appPageLoader = appMain.querySelector('.app-page-loader')
    if (appPageLoader && authState.isAuth) {
      appPageLoader.remove()
    }
  }
}

document.addEventListener('authStateChange', async (event) => {
  const { key, value } = event.detail
  if (key === 'isAuth') {
    if (value) {
      hidePageLoader()
    }
  }
})

showPageLoader()
