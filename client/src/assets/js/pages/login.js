import '~/assets/css/pages/login.css'
import { login } from '~/assets/js/api/auth'

const loginForm = document.querySelector('#login-form')

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault()

  const formData = new FormData(loginForm)
  try {
    const success = await login(formData.get('email'), formData.get('password'))
    if (success) {
      window.location.href = '/'
    }
  } catch (error) {
    console.error(error)
    alert(`Ошибка при входе в систему: ${error.response.data.title}`)
  }
})
