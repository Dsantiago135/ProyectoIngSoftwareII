/** Acceso a consulta de pregunta y operaciones REST actuales de MCReview. */
export class clsReviewsService {
  /** Conserva el cliente HTTP común, sin referencias al DOM. */
  constructor(prmApiClient) { this.attApi = prmApiClient; }
  /** Obtiene los datos de solo lectura de una pregunta desde MCQuestion. */
  opFindQuestion(prmQuestionId) { return this.attApi.opRequest(`/api/questions/${encodeURIComponent(prmQuestionId)}`); }
  /** Consulta las revisiones registradas para una pregunta. */
  opFindByQuestion(prmQuestionId) { return this.attApi.opRequest(`/api/reviews${this.attApi.opQuery({ questionId: prmQuestionId })}`); }
  /** Registra una decisión con justificación obligatoria. */
  opCreate(prmReview) { return this.attApi.opRequest('/api/reviews', { method: 'POST', body: prmReview }); }
}