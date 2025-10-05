import Chart from 'chart.js/auto'
import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'

import '~/assets/css/components/file-statistics.css'
import { formatDate, formatDateTime, formatFileSize } from '~/assets/js/utils'

export function FileStatistics(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('file-statistics-container')
  component.innerHTML = /*html*/ `
    <span class="file-statistics__title">Статистика файла</span>
    <span class="file-statistics__info">
      <span class="file-statistics__info-item">Название: <span class="file-statistics__info-item--name"></span></span>
      <span class="file-statistics__info-item">Размер: <span class="file-statistics__info-item--size"></span></span>
      <span class="file-statistics__info-item">Дата загрузки: <span class="file-statistics__info-item--date"></span></span>
    </span>
    <span class="file-statistics__loader">${LoadingIcon}</span>
    <span class="file-statistics__empty">Файл не найден или нет статистики</span>
    <span class="file-statistics__chart-title">Количество загрузок</span>
    <div style="width: 100%; height: 400px;">
      <canvas class="file-statistics__chart"></canvas>
    </div>
  `

  const fileInfo = component.querySelector('.file-statistics__info')

  const fileName = component.querySelector('.file-statistics__info-item--name')
  const fileSize = component.querySelector('.file-statistics__info-item--size')
  const fileDate = component.querySelector('.file-statistics__info-item--date')

  const containerLoader = component.querySelector('.file-statistics__loader')
  const containerEmpty = component.querySelector('.file-statistics__empty')

  const chartTitle = component.querySelector('.file-statistics__chart-title')
  const chart = component.querySelector('.file-statistics__chart')

  let chartInstance = null

  const renderChart = (statistics) => {
    const labels = statistics.map((item) => formatDate(item.date))
    const data = statistics.map((item) => item.downloads)

    if (chartInstance) {
      chartInstance.destroy()
    }

    chartInstance = new Chart(chart, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            data,
            borderColor: 'rgb(81, 162, 255)',
            backgroundColor: 'rgb(81, 162, 255, 0.2)',
            tension: 0.3,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1,
            },
          },
        },
      },
    })
  }

  document.addEventListener('filesStateChange', (event) => {
    const { key, value } = event.detail
    if (key === 'statistics') {
      containerLoader.style.display = 'none'
      containerEmpty.style.display = value?.data?.length ? 'none' : 'block'
      chartTitle.style.display = value?.data?.length ? 'block' : 'none'
      chart.style.display = value?.data?.length ? 'block' : 'none'

      if (value?.file?.fileName && value?.file?.fileSize && value?.file?.createdAt) {
        fileInfo.style.display = 'flex'

        fileName.textContent = value.file.fileName
        fileSize.textContent = formatFileSize(value.file.fileSize)
        fileDate.textContent = formatDateTime(value.file.createdAt)
      } else {
        fileInfo.style.display = 'none'
      }

      if (value?.data) {
        renderChart(value.data)
      }
    }
  })

  return component
}
