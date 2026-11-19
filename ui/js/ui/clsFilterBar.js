import { opElement } from './opDom.js';

/** Agrupa filtros y una acción para limpiar criterios. */
export class clsFilterBar {
  /** Crea el contenedor semántico de filtros. */
  constructor(prmLabel = 'Filtros') {
    this.attForm = opElement('form', { className: 'filter-bar', 'aria-label': prmLabel });
  }

  /** Añade un campo ya construido al filtro. */
  opAdd(prmField) { this.attForm.append(prmField); }

  /** Devuelve el formulario de filtros. */
  opGetElement() { return this.attForm; }
}