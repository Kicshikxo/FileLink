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
    port: process.env.PORT || 3000,
    allowedHosts: true,
    proxy: {
      '/api': {
        target: process.env.API_URL || 'http://localhost:7070',
        changeOrigin: true,
      },
    },
  },

  preview: {
    port: process.env.PORT || 3000,
    allowedHosts: true,
    proxy: {
      '/api': {
        target: process.env.API_URL || 'http://localhost:7070',
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
        register: resolve(__dirname, 'src/register.html'),
        dashboard: resolve(__dirname, 'src/dashboard.html'),
        information: resolve(__dirname, 'src/information.html'),
      },
    },
  },
})
