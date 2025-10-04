import '~/assets/css/pages/index.css'

const uploadArea = document.querySelector('#index-upload-area')
const progressBar = document.querySelector('#index-progress-bar')
const uploadedFiles = document.querySelector('#index-uploaded-files')

document.addEventListener('filesStateChange', (event) => {
  const { key, value } = event.detail
  if (key === 'uploading') {
    progressBar.style.display = value ? 'flex' : 'none'
  }
  if (key === 'files') {
    uploadedFiles.style.display = value.length ? 'flex' : 'none'
  }
})
