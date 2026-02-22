import Chart from 'chart.js/auto'
import LoadingIcon from '~/assets/icons/line-md--loading-twotone-loop.svg?raw'
import { deleteFile, renameFile } from '~/assets/js/api/files'
import { filesState } from '~/assets/js/state/files'
import { formatDate, formatDateTime, formatFileSize } from '~/assets/js/utils'

import '~/assets/css/components/file-information.css'

export function FileInformation(originalElement) {
  const component = document.createElement('div')
  component.id = originalElement.id ?? ''
  component.className = originalElement.className ?? ''
  component.classList.add('file-information')
  component.innerHTML = /*html*/ `
    <span class="file-information__title">Информация о файле</span>
    <div class="file-information__info">
      <div class="file-information__file-info">
        <span class="file-information__file-info-item">
          Название: <input class="app-input app-input--small file-information__info-item--name"></input>
          <button class="app-button app-button--small app-button--not-allowed-on-disabled rename-file-button" disabled>Переименовать</button>
        </span>
        <span class="file-information__file-info-item">
          Размер: <span class="file-information__info-item--size"></span>
        </span>
        <span class="file-information__file-info-item">
          Дата загрузки: <span class="file-information__info-item--date"></span>
        </span>
        <span class="file-information__file-info-item file-information__info-item-expiration">
          <span class="file-information__info-item--expiration-type"></span>
          <span class="file-information__info-item--expiration-date"></span>
        </span>
      </div>
      <div class="file-information__actions">
        <a class="delete-file">
          <button class="app-button app-button--danger app-button--small delete-file-button">Удалить</button>
        </a>
        <a class="copy-file-link">
          <button class="app-button app-button--primary app-button--small copy-file-link-button" style="width: 100%">Ссылка</button>
        </a>
        <a download class="download-file-link">
          <button class="app-button app-button--success app-button--small" style="width: 100%">Скачать</button>
        </a>
      </div>
    </div>
    <span class="file-information__loader">${LoadingIcon}</span>
    <span class="file-information__empty">Файл не найден или недоступен</span>
    <span class="file-information__chart-title">Количество просмотров/скачиваний</span>
    <div style="width: 100%; height: 300px;">
      <canvas class="file-information__chart"></canvas>
    </div>
  `

  const fileInfo = component.querySelector('.file-information__info')

  const fileName = component.querySelector('.file-information__info-item--name')
  const fileSize = component.querySelector('.file-information__info-item--size')
  const fileDate = component.querySelector('.file-information__info-item--date')
  const fileExpiration = component.querySelector('.file-information__info-item-expiration')
  const fileExpirationType = component.querySelector(
    '.file-information__info-item--expiration-type',
  )
  const fileExpirationDate = component.querySelector(
    '.file-information__info-item--expiration-date',
  )

  const renameFileButton = component.querySelector('.rename-file-button')
  const deleteFileLink = component.querySelector('.delete-file')
  const deleteFileButton = component.querySelector('.delete-file-button')
  const copyFileLink = component.querySelector('.copy-file-link')
  const copyFileLinkButton = component.querySelector('.copy-file-link-button')
  const downloadFileLink = component.querySelector('.download-file-link')

  const containerLoader = component.querySelector('.file-information__loader')
  const containerEmpty = component.querySelector('.file-information__empty')

  const chartTitle = component.querySelector('.file-information__chart-title')
  const chart = component.querySelector('.file-information__chart')

  let chartInstance = null

  fileName.addEventListener('input', () => {
    renameFileButton.disabled = fileName.value === filesState.statistics.file.fileName
  })

  renameFileButton.addEventListener('click', async () => {
    fileName.disabled = true
    renameFileButton.disabled = true
    renameFileButton.classList.remove('app-button--not-allowed-on-disabled')
    try {
      const { success, newFileName } = await renameFile(
        filesState.statistics.file.fileId,
        fileName.value,
      )

      if (success && newFileName) {
        filesState.statistics.file.fileName = newFileName
      }
    } catch (error) {
      console.error(error)
      alert(`Ошибка при переименований файла: ${error.response?.data?.title ?? error.message}`)
    } finally {
      fileName.disabled = false
      fileName.value = filesState.statistics.file.fileName
      renameFileButton.classList.add('app-button--not-allowed-on-disabled')
    }
  })

  deleteFileLink.addEventListener('click', (event) => {
    event.preventDefault()
  })
  deleteFileButton.addEventListener('click', async () => {
    deleteFileButton.disabled = true
    try {
      const success = await deleteFile(filesState.statistics.file.fileId)
      if (success) {
        if (window.history.length > 1 && document.referrer) {
          window.history.back()
        } else {
          window.location.href = '/'
        }
      }
    } catch (error) {
      console.error(error)
      alert(`Ошибка при удалении файла: ${error.response?.data?.title ?? error.message}`)
    } finally {
      deleteFileButton.disabled = false
    }
  })

  copyFileLink.addEventListener('click', (event) => {
    event.preventDefault()
  })
  copyFileLinkButton.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(
        filesState.statistics.file.fileShortId
          ? `${window.location.origin}/id/${filesState.statistics.file.fileShortId}`
          : `${window.location.origin}/api/files/download/${filesState.statistics.file.fileId}`,
      )
      alert(`Ссылка на файл "${filesState.statistics.file.fileName}" скопирована`)
    } catch (error) {
      console.error(error)
    }
  })

  function renderChart(statistics) {
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
      const file = value?.file

      containerLoader.style.display = 'none'
      containerEmpty.style.display = value?.data?.length ? 'none' : 'block'
      chartTitle.style.display = value?.data?.length ? 'block' : 'none'
      chart.style.display = value?.data?.length ? 'block' : 'none'

      if (file?.expiredAt) {
        copyFileLink.style.display = 'none'
        downloadFileLink.style.display = 'none'
      } else {
        copyFileLink.href = file?.fileShortId
          ? `${window.location.origin}/id/${file?.fileShortId}`
          : `${window.location.origin}/api/files/download/${file?.fileId}`
        downloadFileLink.href = `/api/files/download/${file?.fileId}`
      }
      deleteFileLink.href = `/api/files/delete/${file?.fileId}`

      if (file?.fileName && file?.fileSize && file?.createdAt) {
        fileInfo.style.display = 'flex'

        fileName.value = file.fileName
        fileSize.textContent = formatFileSize(file.fileSize)
        fileDate.textContent = formatDateTime(file.createdAt)

        if (file?.expiredAt) {
          fileExpirationType.textContent = 'Файл устарел:'
          fileExpirationDate.textContent = formatDateTime(file.expiredAt)
        } else if (file?.expiresAt) {
          fileExpirationType.textContent = 'Файл устареет:'
          fileExpirationDate.textContent = formatDateTime(file.expiresAt)
        } else {
          fileExpiration.style.display = 'none'
        }
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
