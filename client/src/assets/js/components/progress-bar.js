import '~/assets/css/components/progress-bar.css'

export function ProgressBar(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('progress-bar-container')
  component.innerHTML = /*html*/ `
    <div class="progress-bar">
      <div class="progress-bar__line"></div>
    </div>
    <div class="progress-status">
      Загрузка <span id="progress-percent">0</span>%
    </div>
  `

  const progressBar = component.querySelector('.progress-bar')
  const progressBarLine = progressBar.querySelector('.progress-bar__line')
  const progressPercent = component.querySelector('#progress-percent')

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'progress') {
      progressPercent.textContent = value
      progressBarLine.style.width = `${value}%`
    }
  })

  return component
}
