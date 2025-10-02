import '~/assets/css/pages/index.css'

const uploadArea = document.querySelector('#upload-area')
const fileUploadInput = document.querySelector('#file-upload-input')

uploadArea.addEventListener('click', () => fileUploadInput.click())

uploadArea.addEventListener('dragover', (e) => {
  e.preventDefault()
  uploadArea.classList.add('file-upload-container__upload-area--dragover')
})

uploadArea.addEventListener('dragleave', () => {
  uploadArea.classList.remove('file-upload-container__upload-area--dragover')
})

uploadArea.addEventListener('drop', (e) => {
  e.preventDefault()
  uploadArea.classList.remove('file-upload-container__upload-area--dragover')
  const files = e.dataTransfer.files
  console.log(files)
})

fileUploadInput.addEventListener('change', (e) => {
  console.log(e.target.files)
})
