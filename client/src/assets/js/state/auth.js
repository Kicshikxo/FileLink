import { createState } from '~/assets/js/state'

export const authState = createState(
  {
    isAuth: null,
  },
  (key, value) => {
    document.dispatchEvent(new CustomEvent('authStateChange', { detail: { key, value } }))
  },
)
