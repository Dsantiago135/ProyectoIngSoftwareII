/** Acceso a las operaciones actualmente publicadas por MCQuestion. */
export class clsQuestionsService {
  /** Conserva el cliente del Gateway para este contexto. */
  constructor(prmApiClient) { this.attApi = prmApiClient; }
  /** Crea una pregunta en estado BORRADOR. */
  opCreate(prmQuestion) { return this.attApi.opRequest('/api/questions', { method: 'POST', body: prmQuestion }); }
  /** Consulta una pregunta mediante su identificador. */
  opFindById(prmId) { return this.attApi.opRequest(`/api/questions/${encodeURIComponent(prmId)}`); }
  /** Envía una pregunta a revisión por la ruta existente. */
  opSubmit(prmId) { return this.attApi.opRequest(`/api/questions/${encodeURIComponent(prmId)}/submit`, { method: 'POST' }); }
}