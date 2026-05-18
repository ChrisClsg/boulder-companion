import { defineBoot } from '#q-app/wrappers'
import axios from 'axios'

export const backendUrl =
  window.location.host === 'localhost:5173'
    ? 'http://localhost:8080'
    : window.location.origin

export const api = axios.create({
  baseURL: backendUrl + '/api',
  withCredentials: true,
  withXSRFToken: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
})

export default defineBoot(({ app }) => {
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { axios }
