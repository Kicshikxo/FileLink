import { register } from '~/assets/js/api/auth'
import toast from '~/assets/js/toast'

import '~/assets/css/pages/register.css'

const registerFormContainer = document.querySelector('#register-form-container')
const registerForm = document.querySelector('#register-form')
const registerSubmitButton = document.querySelector('#register-submit-button')

registerForm.addEventListener('submit', async (event) => {
  event.preventDefault()

  registerSubmitButton.disabled = true

  const formData = new FormData(registerForm)

  if (formData.get('password') !== formData.get('password-confirmation')) {
    toast.error('Пароли не совпадают')
    return
  }

  try {
    const success = await register(formData.get('email'), formData.get('password'))
    if (success) {
      window.location.href = '/'
    }
  } catch (error) {
    console.error(error)
    toast.error(`Ошибка при регистрации: ${error.response?.data?.title ?? error.message}`)
  } finally {
    registerSubmitButton.disabled = false
  }
})

window.addEventListener('load', () => {
  registerFormContainer.style.display = 'block'
})
