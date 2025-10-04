import { createState } from '~/assets/js/state'

export const authState = createState(
  {
    isAuth: false,
  },
  (key, value) => {
    document.dispatchEvent(new CustomEvent('authStateChange', { detail: { key, value } }))
  },
)
