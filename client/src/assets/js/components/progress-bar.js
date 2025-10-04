import '~/assets/css/components/progress-bar.css'

export function ProgressBar(originalElement) {
  const progressBarContainer = document.createElement('div')
  progressBarContainer.id = originalElement.id ?? ''
  progressBarContainer.className = originalElement.className ?? ''
  progressBarContainer.classList.add('progress-bar-container')
  progressBarContainer.innerHTML = /*html*/ `
    <div class="progress-bar">
      <div class="progress-bar__line"></div>
    </div>
    <div class="progress-status">
      Загрузка <span id="progress-percent">0</span>%
    </div>
  `

  const progressBar = progressBarContainer.querySelector('.progress-bar')
  const progressBarLine = progressBar.querySelector('.progress-bar__line')

  const progressPercent = progressBarContainer.querySelector('#progress-percent')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'progress') {
      progressPercent.textContent = value
      progressBarLine.style.width = `${value}%`
    }
  })

  return progressBarContainer
}
