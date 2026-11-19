/** Error de API con estado HTTP y detalle de validación del servidor. */
export class clsApiError extends Error {
  /** Construye un error de API a partir de una respuesta HTTP. */
  constructor(prmStatus, prmBody, prmMessage = 'No fue posible completar la solicitud.') {
    super(prmBody?.mensaje || prmBody?.detail || prmMessage);
    this.name = 'clsApiError';
    this.attStatus = prmStatus;
    this.attBody = prmBody;
    this.attFieldErrors = prmBody?.errors || prmBody?.fieldErrors || {};
  }
}