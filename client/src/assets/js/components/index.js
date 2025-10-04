import { FileStatistics } from '~/assets/js/components/file-statistics'
import { Header } from '~/assets/js/components/header'
import { ProgressBar } from '~/assets/js/components/progress-bar'
import { UploadArea } from '~/assets/js/components/upload-area'
import { UploadedFiles } from '~/assets/js/components/uploaded-files'

export function initComponents() {
  document
    .querySelectorAll('[data-component="header"]')
    .forEach((element) => element.replaceWith(Header(element)))
  document
    .querySelectorAll('[data-component="upload-area"]')
    .forEach((element) => element.replaceWith(UploadArea(element)))
  document
    .querySelectorAll('[data-component="progress-bar"]')
    .forEach((element) => element.replaceWith(ProgressBar(element)))
  document
    .querySelectorAll('[data-component="uploaded-files"]')
    .forEach((element) => element.replaceWith(UploadedFiles(element)))
  document
    .querySelectorAll('[data-component="file-statistics"]')
    .forEach((element) => element.replaceWith(FileStatistics(element)))
}
