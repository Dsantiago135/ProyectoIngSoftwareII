/** Acceso exclusivamente de lectura a MCAudit. */
export class clsAuditService {
  /** Conserva el cliente común del Gateway. */
  constructor(prmApiClient) { this.attApi = prmApiClient; }
  /** Busca una página de eventos con filtros opcionales. */
  opSearch(prmFilters) { return this.attApi.opRequest(`/api/audit${this.attApi.opQuery(prmFilters)}`); }
  /** Consulta un evento por su UUID interno. */
  opFindById(prmId) { return this.attApi.opRequest(`/api/audit/${encodeURIComponent(prmId)}`); }
}