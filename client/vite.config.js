import { resolve } from 'path'
import { defineConfig } from 'vite'

export default defineConfig({
  root: 'src',

  resolve: {
    alias: {
      '~': resolve(__dirname, 'src'),
    },
  },

  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:7070',
        changeOrigin: true,
      },
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
