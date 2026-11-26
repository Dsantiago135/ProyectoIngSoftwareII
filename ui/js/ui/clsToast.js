import { opElement, opMascot } from './opDom.js';

/** Muestra mensajes temporales accesibles en la región global de notificaciones. */
export class clsToast {
  /** Guarda el contenedor aria-live donde aparecerán las notificaciones. */
  constructor(prmRegion) { this.attRegion = prmRegion; }

  /** Añade una notificación con expresión de mascota acorde al resultado. */
  opShow(prmMessage, prmType = 'success', prmDuration = 5000) {
    const toast = opElement('div', { className: `toast toast-${prmType}`, role: 'status' });
    toast.append(opMascot(prmType === 'error' ? 'sad' : 'happy', prmType === 'error' ? 'Mascota preocupada' : 'Mascota feliz'));
    toast.append(opElement('p', { text: prmMessage }));
    this.attRegion.append(toast);
    window.setTimeout(() => toast.remove(), prmDuration);
  }
}