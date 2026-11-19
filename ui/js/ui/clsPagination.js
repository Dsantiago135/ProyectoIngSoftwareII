import { opElement } from './opDom.js';

/** Presenta controles de página y tamaño para resultados paginados. */
export class clsPagination {
  /** Crea paginación con callback de cambio de página. */
  constructor(prmOnChange) {
    this.attOnChange = prmOnChange;
    this.attPage = 0;
    this.attTotal = 0;
    this.attSize = 20;
    this.attElement = opElement('nav', { className: 'pagination', 'aria-label': 'Paginación' });
    this.opRender();
  }

  /** Actualiza la página, tamaño y total devueltos por el API. */
  opSetPage(prmPage, prmSize, prmTotal) {
    this.attPage = prmPage;
    this.attSize = prmSize;
    this.attTotal = prmTotal;
    this.opRender();
  }

  /** Dibuja botones accesibles y el indicador del rango visible. */
  opRender() {
    this.attElement.replaceChildren();
    const pages = Math.max(1, Math.ceil(this.attTotal / this.attSize));
    const start = this.attTotal === 0 ? 0 : this.attPage * this.attSize + 1;
    const end = Math.min(this.attTotal, (this.attPage + 1) * this.attSize);
    this.attElement.append(opElement('span', { text: `${start}–${end} de ${this.attTotal}` }));
    const controls = opElement('div', { className: 'panel-toolbar-actions' });
    const previous = opElement('button', { className: 'btn btn-secondary btn-small', type: 'button', text: 'Anterior', disabled: this.attPage <= 0 });
    const next = opElement('button', { className: 'btn btn-secondary btn-small', type: 'button', text: 'Siguiente', disabled: this.attPage + 1 >= pages });
    previous.addEventListener('click', () => this.attOnChange(this.attPage - 1, this.attSize));
    next.addEventListener('click', () => this.attOnChange(this.attPage + 1, this.attSize));
    controls.append(previous, next);
    this.attElement.append(controls);
  }

  /** Devuelve el nodo de paginación. */
  opGetElement() { return this.attElement; }
}