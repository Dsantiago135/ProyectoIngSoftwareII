import { opElement } from './opDom.js';

/** Construye y controla un diálogo modal accesible basado en el elemento dialog. */
export class clsDialog {
  /** Crea el diálogo con título, cuerpo y acciones del pie. */
  constructor(prmTitle, prmBody, prmFooter = []) {
    this.attElement = opElement('dialog', { className: 'dialog', 'aria-label': prmTitle });
    const header = opElement('header', { className: 'dialog-header' }, opElement('h2', { text: prmTitle }));
    const closeButton = opElement('button', { className: 'icon-btn', type: 'button', text: '×', 'aria-label': 'Cerrar diálogo' });
    closeButton.addEventListener('click', () => this.opClose());
    header.append(closeButton);
    this.attBody = opElement('div', { className: 'dialog-body' }, prmBody);
    this.attFooter = opElement('footer', { className: 'dialog-footer' }, prmFooter);
    this.attElement.append(header, this.attBody, this.attFooter);
    this.attElement.addEventListener('click', event => {
      if (event.target === this.attElement) this.opClose();
    });
    document.body.append(this.attElement);
  }

  /** Abre el diálogo y devuelve el nodo dialog para escuchar su cierre. */
  opOpen() { this.attElement.showModal(); return this.attElement; }

  /** Cierra y retira el diálogo del documento. */
  opClose() { if (this.attElement.open) this.attElement.close(); this.attElement.remove(); }

  /** Devuelve el cuerpo para que el panel añada controles dinámicos. */
  opGetBody() { return this.attBody; }
}