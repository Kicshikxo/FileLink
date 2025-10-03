import axios from 'axios'

import '~/assets/css/pages/login.css'

const loginForm = document.querySelector('#login-form')

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault()

  const formData = new FormData(loginForm)
  try {
    const response = await axios.post('/api/auth/login', {
      email: formData.get('email'),
      password: formData.get('password'),
    })
    if (response.data.token) {
      window.location.href = '/'
    }
  } catch (error) {
    console.error(error)
  }
})
