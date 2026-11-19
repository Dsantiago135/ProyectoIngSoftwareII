import { enmRole } from './config/enmRole.js';
import { clsApiClient } from './core/clsApiClient.js';
import { clsAuthSession } from './core/clsAuthSession.js';
import { clsMenu } from './core/clsMenu.js';
import { clsRouter } from './core/clsRouter.js';
import { clsToast } from './ui/clsToast.js';
import { opElement, opInitials, opMascot } from './ui/opDom.js';
import { clsUsersPanel } from './panels/users/clsUsersPanel.js';
import { clsQuestionsPanel } from './panels/questions/clsQuestionsPanel.js';
import { clsReviewsPanel } from './panels/reviews/clsReviewsPanel.js';
import { clsAuditPanel } from './panels/audit/clsAuditPanel.js';
import { clsQuizzesPanel } from './panels/quizzes/clsQuizzesPanel.js';

/** Arranca sesión, menú, rutas hash y módulos independientes de Saber Pro. */
class clsApplication {
  /** Conecta el cliente API, sesión y elementos globales del documento. */
  constructor() {
    this.attSession = new clsAuthSession();
    this.attToast = new clsToast(document.querySelector('#toast-region'));
    this.attApi = new clsApiClient(this.attSession, () => this.opShowLogin());
    this.attPanels = this.opCreatePanelRegistry();
    this.attMenu = new clsMenu(document.querySelector('#app-menu'), this.opCreateMenuItems(), route => this.attRouter.opNavigate(route));
    this.attRouter = new clsRouter(document.querySelector('#app-main'), this.attSession, this.attPanels, this.attMenu);
    this.opBindLogin();
    if (this.attSession.opIsAuthenticated()) this.opShowApplication();
    else this.opShowLogin();
  }

  /** Conecta el formulario de login con el endpoint público de MCUser. */
  opBindLogin() {
    const form = document.querySelector('#login-form');
    form.addEventListener('submit', async event => {
      event.preventDefault();
      const data = new FormData(form);
      const error = document.querySelector('#login-error');
      error.hidden = true;
      const button = form.querySelector('button[type="submit"]');
      button.disabled = true;
      try {
        const response = await this.attApi.opRequest('/api/auth/login', {
          method: 'POST',
          body: { username: data.get('username'), password: data.get('password') }
        });
        this.attSession.opStart(response);
        this.opShowApplication();
      } catch (apiError) {
        error.textContent = apiError.attStatus === 401 ? 'Usuario o contraseña incorrectos.' : apiError.message;
        error.hidden = false;
        document.querySelector('#login-mascot').replaceChildren(opMascot('sad', 'Mascota triste'));
      } finally {
        button.disabled = false;
      }
    });
    document.querySelector('#login-mascot').append(opMascot('thinking', 'Mascota pensando'));
  }

  /** Muestra los elementos de la aplicación, identidad y rutas permitidas. */
  opShowApplication() {
    const identity = this.attSession.opGetIdentity();
    if (!identity) return this.opShowLogin();
    document.querySelector('#login-view').hidden = true;
    document.querySelector('#app-view').hidden = false;
    document.querySelector('#header-username').textContent = identity.username;
    document.querySelector('#header-role').textContent = identity.role;
    document.querySelector('#header-avatar').textContent = opInitials(identity.username);
    document.querySelector('#welcome-mascot').replaceChildren(opMascot('happy', 'Mascota feliz'));
    this.attMenu.opRender(identity.role);
    if (!window.location.hash) this.attRouter.opNavigate(this.attRouter.opDefaultRoute(identity.role));
    else this.attRouter.opRender();
  }

  /** Oculta la app y vuelve al login cuando la sesión expira o el Gateway da 401. */
  opShowLogin() {
    this.attSession.opClear();
    document.querySelector('#app-view').hidden = true;
    document.querySelector('#login-view').hidden = false;
    document.querySelector('#login-form').reset();
  }

  /** Registra cada panel con su propia política de rol y factoría. */
  opCreatePanelRegistry() {
    return {
      users: { roles: [enmRole.ADMIN], create: () => new clsUsersPanel(this.attApi, this.attToast) },
      questions: { roles: [enmRole.AUTHOR, enmRole.ADMIN, enmRole.TEACHER], create: () => new clsQuestionsPanel(this.attApi, this.attToast, this.attSession) },
      reviews: { roles: [enmRole.REVIEWER], create: () => new clsReviewsPanel(this.attApi, this.attToast, this.attSession) },
      audit: { roles: [enmRole.ADMIN], create: () => new clsAuditPanel(this.attApi, this.attToast) },
      quizzes: { roles: [enmRole.STUDENT, enmRole.TEACHER], create: () => new clsQuizzesPanel(this.attApi, this.attToast) }
    };
  }

  /** Describe los destinos de navegación sin acoplarlos entre sí. */
  opCreateMenuItems() {
    return [
      { route: 'users', label: 'Usuarios', icon: 'users', roles: [enmRole.ADMIN] },
      { route: 'questions', label: 'Preguntas', icon: 'question', roles: [enmRole.AUTHOR, enmRole.ADMIN, enmRole.TEACHER] },
      { route: 'reviews', label: 'Revisiones', icon: 'review', roles: [enmRole.REVIEWER] },
      { route: 'audit', label: 'Auditoría', icon: 'audit', roles: [enmRole.ADMIN] },
      { route: 'quizzes', label: 'Quizzes', icon: 'quiz', roles: [enmRole.STUDENT, enmRole.TEACHER] }
    ];
  }
}

new clsApplication();