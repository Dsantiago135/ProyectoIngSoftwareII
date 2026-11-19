import { opElement, opMascot } from '../ui/opDom.js';

/** Base pequeña para encabezados y estados vacíos comunes de los paneles. */
export class clsPanel {
  /** Conserva título, descripción y notificador para un panel. */
  constructor(prmTitle, prmDescription, prmToast) {
    this.attTitle = prmTitle;
    this.attDescription = prmDescription;
    this.attToast = prmToast;
  }

  /** Crea el encabezado estándar del panel con su descripción. */
  opCreateHeading(prmEyebrow = 'Gestión') {
    const copy = opElement('div', { className: 'page-heading-copy' });
    copy.append(opElement('p', { className: 'page-heading-eyebrow', text: prmEyebrow }));
    copy.append(opElement('h1', { text: this.attTitle }));
    copy.append(opElement('p', { className: 'page-subtitle', text: this.attDescription }));
    return opElement('header', { className: 'page-heading' }, copy);
  }

  /** Crea un estado vacío acompañado por la mascota y un mensaje descriptivo. */
  opCreateEmpty(prmTitle, prmMessage, prmExpression = 'thinking') {
    const empty = opElement('section', { className: 'empty-state', role: 'status' });
    empty.append(opMascot(prmExpression, 'Mascota Saber Pro'));
    empty.append(opElement('h2', { text: prmTitle }));
    empty.append(opElement('p', { text: prmMessage }));
    return empty;
  }

  /** Informa que una operación requiere un endpoint aún no publicado. */
  opNotifyUnavailable(prmMessage) {
    this.attToast.opShow(prmMessage, 'error');
  }
}