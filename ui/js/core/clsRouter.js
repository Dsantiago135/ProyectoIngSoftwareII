/** Enrutador por hash que muestra un panel permitido por el rol actual. */
export class clsRouter {
  /** Inicializa la raíz de paneles y la política de acceso por rol. */
  constructor(prmMain, prmSession, prmPanels, prmMenu) {
    this.attMain = prmMain;
    this.attSession = prmSession;
    this.attPanels = prmPanels;
    this.attMenu = prmMenu;
    this.attCurrentPanel = null;
    window.addEventListener('hashchange', () => this.opRender());
  }

  /** Navega a una ruta interna. */
  opNavigate(prmRoute) { window.location.hash = `/${prmRoute}`; }

  /** Resuelve la ruta hash y monta el panel autorizado. */
  async opRender() {
    const identity = this.attSession.opGetIdentity();
    if (!identity) return;
    const route = window.location.hash.replace(/^#\/?/, '') || this.opDefaultRoute(identity.role);
    const panel = this.attPanels[route];
    if (!panel || !panel.roles.includes(identity.role)) {
      this.opNavigate(this.opDefaultRoute(identity.role));
      return;
    }
    this.attMain.replaceChildren();
    this.attMenu.opSetCurrent(route);
    this.attCurrentPanel?.opDestroy?.();
    this.attCurrentPanel = panel.create();
    await this.attCurrentPanel.opMount(this.attMain);
    this.attMain.focus({ preventScroll: true });
  }

  /** Selecciona la primera sección disponible para el rol autenticado. */
  opDefaultRoute(prmRole) {
    return Object.entries(this.attPanels).find(([, panel]) => panel.roles.includes(prmRole))?.[0] || 'quizzes';
  }
}