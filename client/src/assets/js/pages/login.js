import { login } from '~/assets/js/api/auth'

import '~/assets/css/pages/login.css'

const loginForm = document.querySelector('#login-form')
const loginSubmitButton = document.querySelector('#login-submit-button')

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault()

  loginSubmitButton.disabled = true

  const formData = new FormData(loginForm)
  try {
    const success = await login(formData.get('email'), formData.get('password'))
    if (success) {
      window.location.href = '/'
    }
  } catch (error) {
    console.error(error)
    alert(`Ошибка при входе в систему: ${error.response.data.title}`)
  } finally {
    loginSubmitButton.disabled = false
  }
})
