import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  // Proxy API calls to Spring Boot during development
  // so we don't need CORS headers on the backend
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
