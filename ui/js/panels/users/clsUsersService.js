/** Acceso a operaciones de usuarios mediante el cliente común del Gateway. */
export class clsUsersService {
  /** Conserva el único puerto HTTP del panel de usuarios. */
  constructor(prmApiClient) { this.attApi = prmApiClient; }
  /** Obtiene todos los usuarios administrables. */
  opFindAll() { return this.attApi.opRequest('/api/users'); }
  /** Registra un usuario creado por un ADMIN. */
  opCreate(prmUser) { return this.attApi.opRequest('/api/users', { method: 'POST', body: prmUser }); }
  /** Actualiza el usuario indicado. */
  opUpdate(prmId, prmUser) { return this.attApi.opRequest(`/api/users/${encodeURIComponent(prmId)}`, { method: 'PUT', body: prmUser }); }
  /** Desactiva una cuenta sin eliminarla. */
  opDeactivate(prmId) { return this.attApi.opRequest(`/api/users/${encodeURIComponent(prmId)}/deactivate`, { method: 'PATCH' }); }
}