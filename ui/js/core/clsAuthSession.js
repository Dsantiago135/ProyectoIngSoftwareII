import { appConfig } from '../config/appConfig.js';

/** Mantiene el JWT de sesión en sessionStorage y decodifica sus claims de presentación. */
export class clsAuthSession {
  /** Carga una sesión válida o elimina el token vencido. */
  constructor() {
    this.attSession = this.opReadStoredSession();
    if (this.attSession && this.opIsExpired(this.attSession.claims)) this.opClear();
  }

  /** Registra el JWT recibido del endpoint público de login. */
  opStart(prmTokenResponse) {
    const claims = this.opDecodeToken(prmTokenResponse?.token);
    if (!prmTokenResponse?.token || !claims?.role) throw new Error('El servidor no entregó un token válido.');
    this.attSession = { token: prmTokenResponse.token, claims, expiresAt: prmTokenResponse.expiresAt };
    sessionStorage.setItem(appConfig.sessionKey, JSON.stringify(this.attSession));
    return this.opGetIdentity();
  }

  /** Devuelve el token si la sesión sigue vigente. */
  opGetToken() {
    if (this.attSession && this.opIsExpired(this.attSession.claims)) this.opClear();
    return this.attSession?.token || null;
  }

  /** Indica si existe una sesión con token vigente. */
  opIsAuthenticated() { return Boolean(this.opGetToken()); }

  /** Expone identidad mínima para menú y presentación, no para decisiones de seguridad. */
  opGetIdentity() {
    const claims = this.attSession?.claims;
    return claims ? { id: claims.sub, username: claims.username || claims.sub, role: claims.role } : null;
  }

  /** Borra el token de la pestaña cuando vence o el Gateway responde 401. */
  opClear() {
    this.attSession = null;
    sessionStorage.removeItem(appConfig.sessionKey);
  }

  /** Lee y valida la estructura del token previamente almacenado. */
  opReadStoredSession() {
    try {
      const session = JSON.parse(sessionStorage.getItem(appConfig.sessionKey));
      if (!session?.token || !session?.claims) return null;
      return session;
    } catch { return null; }
  }

  /** Decodifica claims JWT para presentación; el Gateway es quien valida la firma. */
  opDecodeToken(prmToken) {
    try {
      const segment = prmToken.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const decoded = atob(segment.padEnd(Math.ceil(segment.length / 4) * 4, '='));
      return JSON.parse(decodeURIComponent(Array.from(decoded, char => `%${char.charCodeAt(0).toString(16).padStart(2, '0')}`).join('')));
    } catch { return null; }
  }

  /** Comprueba el claim exp sin sustituir la validación del Gateway. */
  opIsExpired(prmClaims) { return !prmClaims?.exp || Number(prmClaims.exp) * 1000 <= Date.now(); }
}