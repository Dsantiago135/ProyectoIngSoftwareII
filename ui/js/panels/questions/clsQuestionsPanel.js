import { enmQuestionStatus } from '../../config/enmQuestionStatus.js';
import { clsDialog } from '../../ui/clsDialog.js';
import { clsFilterBar } from '../../ui/clsFilterBar.js';
import { clsTable } from '../../ui/clsTable.js';
import { opElement, opText } from '../../ui/opDom.js';
import { clsPanel } from '../clsPanel.js';
import { clsQuestionForm } from './clsQuestionForm.js';
import { clsQuestionsService } from './clsQuestionsService.js';
import { clsQuestionValidator } from './clsQuestionValidator.js';

/** Panel AUTHOR/ADMIN/TEACHER para crear y consultar preguntas. */
export class clsQuestionsPanel extends clsPanel {
  /** Conserva sesión, servicios y validador del propio panel. */
  constructor(prmApi, prmToast, prmSession) {
    super('Banco de preguntas', 'Crea, consulta y prepara preguntas para Saber Pro.', prmToast);
    this.attService = new clsQuestionsService(prmApi);
    this.attValidator = new clsQuestionValidator();
    this.attQuestionForm = new clsQuestionForm(this.attValidator, prmToast);
    this.attSession = prmSession;
    this.attQuestions = new Map();
  }

  /** Monta filtros informativos, búsqueda disponible, métricas y tabla local. */
  async opMount(prmRoot) {
    const stack = opElement('div', { className: 'panel-stack questions-panel' });
    const heading = this.opCreateHeading('Banco académico');
    if (this.attSession.opGetIdentity()?.role === 'AUTHOR') {
      const create = opElement('button', { className: 'btn btn-primary', type: 'button', text: 'Nueva pregunta' });
      create.addEventListener('click', () => this.opOpenCreate());
      heading.append(create);
    }
    stack.append(heading);
    stack.append(opElement('p', { className: 'notice', text: 'La API todavía no ofrece listado con filtros, edición, reapertura, publicación ni archivo. La búsqueda por ID y la creación sí están disponibles.' }));
    stack.append(this.opCreateFilters());
    const searchForm = opElement('form', { className: 'panel-toolbar card' });
    const id = opElement('input', { id: 'question-id-search', className: 'field-control', name: 'id', placeholder: 'UUID de la pregunta', 'aria-label': 'UUID de la pregunta' });
    const searchButton = opElement('button', { className: 'btn btn-secondary', type: 'submit', text: 'Consultar por ID' });
    searchForm.append(opElement('label', { className: 'field', htmlFor: 'question-id-search' }, opElement('span', { className: 'field-label', text: 'Consulta individual' }), id), searchButton);
    searchForm.addEventListener('submit', event => { event.preventDefault(); this.opLoadById(id.value); });
    stack.append(searchForm);
    this.attStats = opElement('section', { className: 'card bar-chart', 'aria-label': 'Preguntas por estado' });
    this.attStats.append(opElement('h2', { text: 'Preguntas por estado' }));
    this.attStatsRows = opElement('div', { className: 'bar-chart-rows' });
    this.attStats.append(this.attStatsRows);
    stack.append(this.attStats);
    this.attTable = new clsTable([
      { key: 'id', label: 'Identificador', render: question => opElement('code', { text: question.id }) },
      { key: 'directQuestion', label: 'Pregunta', render: question => opElement('span', { className: 'questions-panel-question', text: question.directQuestion }) },
      { key: 'topic', label: 'Tema' },
      { key: 'status', label: 'Estado', render: question => opElement('span', { className: `badge badge-${String(question.status).toLocaleLowerCase('es')}`, text: question.status }) },
      { key: 'actions', label: 'Acciones', render: question => this.opRowActions(question) }
    ], 'Preguntas consultadas y creadas en esta sesión');
    const tableCard = opElement('section', { className: 'card table-scroll', 'aria-label': 'Preguntas' });
    tableCard.append(this.attTable.opGetElement());
    stack.append(tableCard);
    this.attEmpty = this.opCreateEmpty('Aún no hay preguntas cargadas', 'Consulta una pregunta por UUID o crea un borrador.', 'thinking');
    stack.append(this.attEmpty);
    prmRoot.append(stack);
    this.opRenderLocal();
  }

  /** Presenta controles de filtros que esperan el futuro endpoint GET de colección. */
  opCreateFilters() {
    const filterBar = new clsFilterBar('Filtros de preguntas, disponibles cuando el API exponga listado');
    const definitions = [
      ['competence', 'Competencia'], ['topic', 'Tema'], ['subtopic', 'Subtema'],
      ['difficulty', 'Dificultad'], ['status', 'Estado']
    ];
    definitions.forEach(([name, label]) => {
      const input = opElement('input', { id: `question-filter-${name}`, className: 'field-control', disabled: true, 'aria-label': label });
      filterBar.opAdd(opElement('label', { className: 'field', htmlFor: `question-filter-${name}` }, opElement('span', { className: 'field-label', text: label }), input));
    });
    return filterBar.opGetElement();
  }

  /** Abre creación si la política del panel asigna ese permiso al rol. */
  opOpenCreate() {
    if (this.attSession.opGetIdentity()?.role !== 'AUTHOR') {
      this.attToast.opShow('Solo AUTHOR puede crear preguntas según la regla actual del Gateway.', 'error');
      return;
    }
    this.attQuestionForm.opOpen(request => this.opCreate(request));
  }

  /** Guarda una pregunta y la añade a la tabla de la sesión. */
  async opCreate(prmRequest) {
    const question = await this.attService.opCreate(prmRequest);
    this.attQuestions.set(question.id, question);
    this.opRenderLocal();
    this.attToast.opShow('Borrador de pregunta creado.');
  }

  /** Consulta una pregunta mediante el endpoint GET individual disponible. */
  async opLoadById(prmId) {
    if (!prmId.trim()) { this.attToast.opShow('Ingresa el UUID de la pregunta.', 'error'); return; }
    try {
      const question = await this.attService.opFindById(prmId.trim());
      this.attQuestions.set(question.id, question);
      this.opRenderLocal();
    } catch (error) { this.attToast.opShow(error.message, 'error'); }
  }

  /** Renderiza la tabla y gráficos con los registros vistos en esta sesión. */
  opRenderLocal() {
    const questions = [...this.attQuestions.values()];
    this.attTable.opRender(questions);
    this.attEmpty.hidden = questions.length > 0;
    this.attStatsRows.replaceChildren();
    const counts = Object.values(enmQuestionStatus).map(status => [status, questions.filter(question => question.status === status).length]);
    counts.forEach(([status, count]) => {
      const row = opElement('div', { className: 'bar-chart-row' });
      row.append(opElement('span', { className: `badge badge-${status.toLocaleLowerCase('es')}`, text: status }));
      const track = opElement('div', { className: 'bar-chart-track', role: 'img', 'aria-label': `${count} preguntas en estado ${status}` });
      for (let index = 0; index < Math.min(count, 12); index += 1) track.append(opElement('span', { className: `bar-chart-fill bar-chart-fill-${status.toLocaleLowerCase('es')}` }));
      row.append(track, opElement('strong', { text: count }));
      this.attStatsRows.append(row);
    });
  }

  /** Devuelve acciones válidas según el rol/propiedad, deshabilitando rutas ausentes. */
  opRowActions(prmQuestion) {
    const identity = this.attSession.opGetIdentity();
    const actions = opElement('div', { className: 'tbl-actions' });
    actions.append(this.opAction('Ver', () => this.opShowDetails(prmQuestion)));
    const isOwner = identity?.id === prmQuestion.authorId;
    if (prmQuestion.status === enmQuestionStatus.BORRADOR && identity?.role === 'AUTHOR' && isOwner) {
      actions.append(this.opAction('Editar', () => this.opExplainUnavailable('La API no tiene PUT /api/questions/{id}.'), true));
      actions.append(this.opAction('Enviar', () => this.opSubmit(prmQuestion.id)));
    } else if (prmQuestion.status === enmQuestionStatus.RECHAZADA && identity?.role === 'AUTHOR' && isOwner) {
      actions.append(this.opAction('Reabrir', () => this.opExplainUnavailable('La API no tiene POST /api/questions/{id}/reopen.'), true));
    } else if (prmQuestion.status === enmQuestionStatus.APROBADA && identity?.role === 'ADMIN') {
      actions.append(this.opAction('Publicar', () => this.opExplainUnavailable('La API no tiene POST /api/questions/{id}/publish.'), true));
    } else if (prmQuestion.status === enmQuestionStatus.PUBLICADA && identity?.role === 'ADMIN') {
      actions.append(this.opAction('Archivar', () => this.opExplainUnavailable('La API no tiene POST /api/questions/{id}/archive.'), true));
    }
    return actions;
  }

  /** Crea un botón de acción que explica por qué una ruta puede estar deshabilitada. */
  opAction(prmLabel, prmAction, prmDisabled = false) {
    const button = opElement('button', { className: 'btn btn-secondary btn-small', type: 'button', text: prmLabel, disabled: prmDisabled });
    button.addEventListener('click', prmAction);
    return button;
  }

  /** Envía la pregunta usando el endpoint ya disponible. */
  async opSubmit(prmId) {
    try {
      const question = await this.attService.opSubmit(prmId);
      this.attQuestions.set(question.id, question);
      this.opRenderLocal();
      this.attToast.opShow('Pregunta enviada a revisión.');
    } catch (error) { this.attToast.opShow(error.message, 'error'); }
  }

  /** Muestra detalles con nodos de texto seguros. */
  opShowDetails(prmQuestion) {
    const body = opElement('dl', { className: 'detail-list' });
    [['Contexto', prmQuestion.context], ['Pregunta', prmQuestion.directQuestion], ['Respuesta', prmQuestion.correctAnswer],
      ['Justificación', prmQuestion.justification], ['Distractores', prmQuestion.distractors?.join(' · ')],
      ['Estado', prmQuestion.status], ['Tema', prmQuestion.topic]].forEach(([term, value]) => {
      body.append(opElement('dt', { text: term }), opElement('dd', { text: opText(value) }));
    });
    const close = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Cerrar' });
    const dialog = new clsDialog('Detalle de pregunta', body, [close]);
    close.addEventListener('click', () => dialog.opClose());
    dialog.opOpen();
  }

  /** Notifica que falta implementar la ruta indicada en el contrato REST. */
  opExplainUnavailable(prmMessage) { this.attToast.opShow(prmMessage, 'error'); }
}