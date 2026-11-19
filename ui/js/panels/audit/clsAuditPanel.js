import { appConfig } from '../../config/appConfig.js';
import { clsDialog } from '../../ui/clsDialog.js';
import { clsFilterBar } from '../../ui/clsFilterBar.js';
import { clsPagination } from '../../ui/clsPagination.js';
import { clsTable } from '../../ui/clsTable.js';
import { opElement } from '../../ui/opDom.js';
import { clsPanel } from '../clsPanel.js';
import { clsAuditService } from './clsAuditService.js';

/** Panel ADMIN de consulta de eventos, sin operaciones de escritura. */
export class clsAuditPanel extends clsPanel {
  /** Conserva el servicio de lectura y filtros actuales. */
  constructor(prmApi, prmToast) {
    super('Auditoría', 'Consulta cronológica de eventos registrados en el sistema.', prmToast);
    this.attService = new clsAuditService(prmApi);
    this.attFilters = { page: 0, size: appConfig.auditPageSize };
  }

  /** Monta filtros por actor/entidad/fechas y tabla paginada. */
  async opMount(prmRoot) {
    const stack = opElement('div', { className: 'panel-stack audit-panel' });
    stack.append(this.opCreateHeading('Trazabilidad'));
    const form = opElement('form', { className: 'filter-bar card', 'aria-label': 'Filtros de auditoría' });
    form.append(this.opFilter('actorId', 'Actor UUID'));
    form.append(this.opFilter('entityType', 'Tipo de entidad'));
    form.append(this.opFilter('entityId', 'Entidad UUID'));
    form.append(this.opFilter('from', 'Desde', 'datetime-local'));
    form.append(this.opFilter('to', 'Hasta', 'datetime-local'));
    const submit = opElement('button', { className: 'btn btn-primary', type: 'submit', text: 'Aplicar filtros' });
    const clear = opElement('button', { className: 'btn btn-secondary', type: 'reset', text: 'Limpiar' });
    form.append(submit, clear);
    form.addEventListener('submit', event => { event.preventDefault(); this.opApplyFilters(form); });
    form.addEventListener('reset', () => window.setTimeout(() => { this.attFilters = { page: 0, size: appConfig.auditPageSize }; this.opLoad(); }, 0));
    stack.append(form);
    this.attTable = new clsTable([
      { key: 'occurredAt', label: 'Fecha del evento', render: event => new Date(event.occurredAt).toLocaleString('es-CO') },
      { key: 'action', label: 'Acción', render: event => opElement('span', { className: 'audit-panel-action', text: event.action }) },
      { key: 'sourceService', label: 'Servicio' },
      { key: 'actorId', label: 'Actor' },
      { key: 'entityType', label: 'Entidad' },
      { key: 'detail', label: 'Detalle', render: record => this.opDetailButton(record) }
    ], 'Registros de auditoría');
    const card = opElement('section', { className: 'card table-scroll', 'aria-label': 'Registros de auditoría' });
    card.append(this.attTable.opGetElement());
    this.attPagination = new clsPagination((page, size) => { this.attFilters.page = page; this.attFilters.size = size; this.opLoad(); });
    card.append(this.attPagination.opGetElement());
    stack.append(card);
    this.attEmpty = this.opCreateEmpty('Sin eventos', 'Ajusta los filtros o vuelve a consultar más tarde.');
    stack.append(this.attEmpty);
    prmRoot.append(stack);
    await this.opLoad();
  }

  /** Crea un filtro con etiqueta asociada y nombre compatible con la API. */
  opFilter(prmName, prmLabel, prmType = 'text') {
    const id = `audit-${prmName}`;
    const input = opElement('input', { id, name: prmName, className: 'field-control', type: prmType });
    return opElement('label', { className: 'field', htmlFor: id }, opElement('span', { className: 'field-label', text: prmLabel }), input);
  }

  /** Convierte filtros de fecha local a ISO-8601 y comienza desde la primera página. */
  opApplyFilters(prmForm) {
    const values = Object.fromEntries(new FormData(prmForm).entries());
    ['from', 'to'].forEach(name => { if (values[name]) values[name] = new Date(values[name]).toISOString(); });
    this.attFilters = { ...values, page: 0, size: appConfig.auditPageSize };
    this.opLoad();
  }

  /** Solicita una página de eventos sin ofrecer operaciones de escritura. */
  async opLoad() {
    try {
      const result = await this.attService.opSearch(this.attFilters);
      this.attTable.opRender(result.content || []);
      this.attPagination.opSetPage(result.page || 0, result.size || this.attFilters.size, result.totalElements || 0);
      this.attEmpty.hidden = Boolean(result.content?.length);
    } catch (error) { this.attToast.opShow(error.message, 'error'); }
  }

  /** Crea botón que muestra el detalle JSON en un diálogo de solo lectura. */
  opDetailButton(prmRecord) {
    const button = opElement('button', { className: 'btn btn-secondary btn-small', type: 'button', text: 'Ver detalle' });
    button.addEventListener('click', () => this.opShowDetail(prmRecord));
    return button;
  }

  /** Presenta campos de evento y JSON usando exclusivamente textContent. */
  opShowDetail(prmRecord) {
    const body = opElement('div', { className: 'panel-stack' });
    body.append(opElement('p', { text: `ID de evento: ${prmRecord.eventId}` }));
    body.append(opElement('p', { text: `Origen: ${prmRecord.sourceService} · Acción: ${prmRecord.action}` }));
    let detail = prmRecord.detail;
    try { if (typeof detail === 'string') detail = JSON.parse(detail); } catch { detail = prmRecord.detail; }
    body.append(opElement('pre', { className: 'audit-panel-detail', text: JSON.stringify(detail, null, 2) }));
    const close = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Cerrar' });
    const dialog = new clsDialog('Detalle de auditoría', body, [close]);
    close.addEventListener('click', () => dialog.opClose());
    dialog.opOpen();
  }
}