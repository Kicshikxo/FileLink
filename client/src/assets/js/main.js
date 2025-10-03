import axios from 'axios'

import '~/assets/css/main.css'
import '~/assets/css/rubik.fontface.css'

async function checkAuth() {
  try {
    const response = await axios.get('/api/auth/check')

    if (response.status === 200) {
      if (window.location.pathname === '/login') {
        window.location.href = '/'
      }
    }
  } catch (error) {
    if (error.response.status === 401 && window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
  }
}

checkAuth()
