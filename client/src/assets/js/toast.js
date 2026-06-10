import Toastify from 'toastify-js'
import 'toastify-js/src/toastify.css'

const toast = {
  info(message) {
    Toastify({
      text: message,
      duration: 3000,
      gravity: 'top',
      position: 'center',
      className: 'app-toast',
      offset: { y: -3 },
    }).showToast()
  },
  success(message) {
    Toastify({
      text: message,
      duration: 3000,
      gravity: 'top',
      position: 'center',
      className: 'app-toast app-toast--success',
      offset: { y: -3 },
    }).showToast()
  },
  error(message) {
    Toastify({
      text: message,
      duration: 4000,
      gravity: 'top',
      position: 'center',
      className: 'app-toast app-toast--error',
      stopOnFocus: true,
    }).showToast()
  },
}

export default toast
