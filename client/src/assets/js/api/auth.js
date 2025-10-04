import axios from 'axios'

export async function checkAuth() {
  const response = await axios.get('/api/auth/check')

  return response.status === 200
}

export async function login(email, password) {
  const response = await axios.post('/api/auth/login', { email, password })

  return !!response.data.token
}

export async function logout() {
  const response = await axios.get('/api/auth/logout')

  return response.status === 200
}
