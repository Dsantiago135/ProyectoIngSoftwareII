import { opElement } from '../../ui/opDom.js';
import { clsPanel } from '../clsPanel.js';

/** Panel placeholder de quizzes para STUDENT y TEACHER. */
export class clsQuizzesPanel extends clsPanel {
  /** Inicializa el panel futuro sin asumir endpoints inexistentes. */
  constructor(prmApi, prmToast) { super('Quizzes', 'Práctica y seguimiento académico.', prmToast); }

  /** Presenta el estado futuro sin llamar a un microservicio aún no creado. */
  async opMount(prmRoot) {
    const content = opElement('div', { className: 'panel-stack quizzes-panel' });
    content.append(this.opCreateHeading('Entrenamiento'));
    const empty = this.opCreateEmpty('Próximamente', 'El microservicio de quizzes aún no está disponible.', 'happy');
    content.append(empty);
    prmRoot.append(content);
  }
}