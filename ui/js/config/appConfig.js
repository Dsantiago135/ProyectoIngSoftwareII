/** Configuración central de la interfaz y del API Gateway. */
export const appConfig = Object.freeze({
  apiBaseUrl: document.querySelector('meta[name="api-base-url"]')?.content || 'http://localhost:8080',
  sessionKey: 'saberpro.session',
  defaultPageSize: 10,
  auditPageSize: 20
});