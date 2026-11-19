import { clsPanel } from '../clsPanel.js';
import { clsReviewsService } from './clsReviewsService.js';
import { clsReviewForm } from './clsReviewForm.js';
import { clsTable } from '../../ui/clsTable.js';
import { opElement } from '../../ui/opDom.js';

/** Panel REVIEWER para consultar una pregunta pendiente y registrar revisiones. */
export class clsReviewsPanel extends clsPanel {
  /** Conserva el servicio de revisiones, formulario y sesión actual. */
  constructor(prmApi, prmToast, prmSession) {
    super('Revisiones', 'Evalúa preguntas con una decisión clara y justificación.', prmToast);
    this.attService = new clsReviewsService(prmApi);
    this.attReviewForm = new clsReviewForm(prmToast);
    this.attSession = prmSession;
    this.attQuestion = null;
    this.attReviews = [];
  }

  /** Monta búsqueda manual; la API actual no tiene una ruta de cola pendiente. */
  async opMount(prmRoot) {
    const stack = opElement('div', { className: 'panel-stack reviews-panel' });
    stack.append(this.opCreateHeading('Evaluación académica'));
    stack.append(opElement('p', { className: 'notice', text: 'La API aún no ofrece una cola de preguntas pendientes. Ingresa el UUID de una pregunta en revisión para cargarla.' }));
    const search = opElement('form', { className: 'card panel-toolbar' });
    const id = opElement('input', { id: 'review-question-id', className: 'field-control', name: 'questionId', required: true, placeholder: 'UUID de la pregunta' });
    const button = opElement('button', { className: 'btn btn-secondary', type: 'submit', text: 'Cargar pregunta' });
    search.append(opElement('label', { className: 'field', htmlFor: 'review-question-id' }, opElement('span', { className: 'field-label', text: 'Pregunta pendiente' }), id), button);
    search.addEventListener('submit', event => { event.preventDefault(); this.opLoad(id.value.trim()); });
    stack.append(search);
    this.attContent = opElement('div', { className: 'reviews-panel-content' });
    this.attEmpty = this.opCreateEmpty('Selecciona una pregunta', 'Al cargar su UUID se mostrarán el contenido y las revisiones registradas.', 'thinking');
    this.attContent.append(this.attEmpty);
    stack.append(this.attContent);
    prmRoot.append(stack);
  }

  /** Consulta pregunta e historial por sus endpoints existentes. */
  async opLoad(prmQuestionId) {
    try {
      const [question, reviews] = await Promise.all([
        this.attService.opFindQuestion(prmQuestionId),
        this.attService.opFindByQuestion(prmQuestionId)
      ]);
      this.attQuestion = question;
      this.attReviews = reviews || [];
      this.opRenderQuestion();
    } catch (error) { this.attToast.opShow(error.message, 'error'); }
  }

  /** Renderiza la pregunta en modo lectura, el historial y progreso. */
  opRenderQuestion() {
    this.attContent.replaceChildren();
    this.attEmpty.hidden = true;
    const header = opElement('section', { className: 'card panel-toolbar' });
    const copy = opElement('div');
    copy.append(opElement('h2', { text: 'Pregunta en revisión' }));
    copy.append(opElement('p', { className: 'page-subtitle', text: `Estado: ${this.attQuestion.status} · ID: ${this.attQuestion.id}` }));
    header.append(copy, this.opProgress());
    const question = opElement('article', { className: 'reviews-panel-read-only questions-panel-question' });
    [['Contexto', this.attQuestion.context], ['Pregunta directa', this.attQuestion.directQuestion], ['Respuesta correcta', this.attQuestion.correctAnswer],
      ['Distractores', this.attQuestion.distractors?.join(' · ')], ['Justificación', this.attQuestion.justification], ['Bibliografía', this.attQuestion.bibliography]]
      .forEach(([label, value]) => { if (value) question.append(opElement('h3', { text: label }), opElement('p', { text: value })); });
    const history = new clsTable([
      { key: 'reviewerId', label: 'Revisor' },
      { key: 'decision', label: 'Decisión', render: review => opElement('span', { className: `badge ${review.decision === 'APPROVED' ? 'badge-aprobada' : 'badge-rechazada'}`, text: review.decision === 'APPROVED' ? 'Aprobada' : 'Rechazada' }) },
      { key: 'justification', label: 'Justificación' },
      { key: 'createdAt', label: 'Fecha', render: review => new Date(review.createdAt).toLocaleString('es-CO') }
    ], 'Revisiones previas');
    const historyCard = opElement('section', { className: 'card table-scroll' });
    historyCard.append(opElement('h2', { text: 'Revisiones registradas' }), history.opGetElement());
    history.opRender(this.attReviews);
    this.attContent.append(header, question, historyCard);
    const canReview = ['PENDIENTE_REVISION', 'EN_REVISION'].includes(this.attQuestion.status) && this.attReviews.length < 2;
    if (canReview) {
      const reviewButton = opElement('button', { className: 'btn btn-primary', type: 'button', text: 'Registrar mi revisión' });
      reviewButton.addEventListener('click', () => this.attReviewForm.opOpen(this.attQuestion.id, request => this.opCreate(request)));
      this.attContent.append(reviewButton);
    } else {
      this.attContent.append(opElement('p', { className: 'notice', text: 'La pregunta ya no admite nuevas revisiones.' }));
    }
  }

  /** Dibuja el progreso del proceso, limitado a dos revisiones. */
  opProgress() {
    const count = Math.min(this.attReviews.length, 2);
    const ring = opElement('div', { className: `progress-ring progress-ring--${count}`, role: 'img', 'aria-label': `${count} de 2 revisiones` });
    ring.append(opElement('span', { className: 'progress-ring-inner', text: `${count}/2` }));
    return ring;
  }

  /** Registra una decisión, notifica y recarga el estado compartido. */
  async opCreate(prmRequest) {
    await this.attService.opCreate(prmRequest);
    this.attToast.opShow('Revisión registrada.', 'success');
    await this.opLoad(prmRequest.questionId);
  }
}