import { createState } from '~/assets/js/state'

export const filesState = createState(
  {
    checkFiles: false,
    uploading: false,
    progress: 0,
    files: undefined,
    statistics: undefined,
  },
  (key, value) => {
    document.dispatchEvent(new CustomEvent('filesStateChange', { detail: { key, value } }))
  },
)
