import '~/assets/css/components/progress-bar.css'

export function ProgressBar(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('progress-bar')
  component.innerHTML = /*html*/ `
    <div class="progress-bar__progress">
      <div class="progress-bar__progress-line"></div>
    </div>
    <div class="progress-bar__status">
      Загрузка <span class="progress-bar__percent">0</span>%
    </div>
  `

  const progressBar = component.querySelector('.progress-bar__progress')
  const progressBarLine = progressBar.querySelector('.progress-bar__progress-line')
  const progressPercent = component.querySelector('.progress-bar__percent')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'progress') {
      progressPercent.textContent = value
      progressBarLine.style.width = `${value}%`
    }
  })

  return component
}
