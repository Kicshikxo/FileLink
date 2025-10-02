import { resolve } from 'path'
import { defineConfig } from 'vite'

export default defineConfig({
  root: 'src',

  resolve: {
    alias: {
      '~': resolve(__dirname, 'src'),
    },
  },

  build: {
    outDir: '../dist',
    emptyOutDir: true,

    rollupOptions: {
      input: {
        index: resolve(__dirname, 'src/index.html'),
        login: resolve(__dirname, 'src/login.html'),
        dashboard: resolve(__dirname, 'src/dashboard.html'),
      },
    },
  },
})
