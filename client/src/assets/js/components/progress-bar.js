import '~/assets/css/components/progress-bar.css'

export function ProgressBar(originalElement) {
  const container = document.createElement('div')
  container.id = originalElement.id ?? ''
  container.className = originalElement.className ?? ''
  container.classList.add('progress-bar-container')
  container.innerHTML = /*html*/ `
    <div class="progress-bar">
      <div class="progress-bar__line"></div>
    </div>
    <div class="progress-status">
      Загрузка <span id="progress-percent">0</span>%
    </div>
  `

  const progressBar = container.querySelector('.progress-bar')
  const progressBarLine = progressBar.querySelector('.progress-bar__line')
  const progressPercent = container.querySelector('#progress-percent')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'progress') {
      progressPercent.textContent = value
      progressBarLine.style.width = `${value}%`
    }
  })

  return container
}
