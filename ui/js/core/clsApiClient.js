import { appConfig } from '../config/appConfig.js';
import { clsApiError } from './clsApiError.js';

/** Cliente HTTP único de la interfaz; todos los servicios hablan con el Gateway. */
export class clsApiClient {
  /** Guarda el origen de API y las funciones de sesión. */
  constructor(prmSession, prmOnUnauthorized, prmBaseUrl = appConfig.apiBaseUrl) {
    this.attSession = prmSession;
    this.attOnUnauthorized = prmOnUnauthorized;
    this.attBaseUrl = prmBaseUrl.replace(/\/$/, '');
  }

  /** Envía una solicitud HTTP y devuelve JSON, respuesta vacía o lanza clsApiError. */
  async opRequest(prmPath, prmOptions = {}) {
    const headers = new Headers(prmOptions.headers || {});
    const token = this.attSession?.opGetToken();
    if (token) headers.set('Authorization', `Bearer ${token}`);
    if (prmOptions.body !== undefined && !(prmOptions.body instanceof FormData)) {
      headers.set('Content-Type', 'application/json');
    }
    const response = await fetch(`${this.attBaseUrl}${prmPath}`, {
      ...prmOptions,
      headers,
      body: prmOptions.body instanceof FormData ? prmOptions.body : prmOptions.body === undefined ? undefined : JSON.stringify(prmOptions.body)
    });
    if (response.status === 401 && this.attSession?.opIsAuthenticated()) {
      this.attOnUnauthorized?.();
      throw new clsApiError(401, null, 'La sesión venció. Ingresa de nuevo.');
    }
    const body = await this.opReadBody(response);
    if (!response.ok) throw new clsApiError(response.status, body);
    return body;
  }

  /** Convierte parámetros de consulta en una cadena URL codificada. */
  opQuery(prmValues) {
    const query = new URLSearchParams();
    Object.entries(prmValues || {}).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') query.set(key, String(value));
    });
    const encoded = query.toString();
    return encoded ? `?${encoded}` : '';
  }

  /** Lee respuestas JSON sin fallar cuando el servidor devuelve 204 o un cuerpo vacío. */
  async opReadBody(prmResponse) {
    if (prmResponse.status === 204) return null;
    const text = await prmResponse.text();
    if (!text) return null;
    try { return JSON.parse(text); }
    catch { return { mensaje: text }; }
  }
}