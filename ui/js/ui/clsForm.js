/** Ayudante para leer campos y mostrar errores del servidor en formularios nativos. */
export class clsForm {
  /** Vincula el ayudante con un formulario HTML existente. */
  constructor(prmForm) { this.attForm = prmForm; }

  /** Devuelve los valores del formulario como un objeto plano. */
  opGetValues() { return Object.fromEntries(new FormData(this.attForm).entries()); }

  /** Asigna errores por nombre de campo usando textContent y aria-invalid. */
  opSetErrors(prmErrors) {
    Object.entries(prmErrors || {}).forEach(([name, message]) => {
      const field = this.attForm.elements.namedItem(name);
      const label = this.attForm.querySelector(`[data-error-for="${CSS.escape(name)}"]`);
      if (label) label.textContent = Array.isArray(message) ? message.join(' ') : String(message);
      if (field) field.setAttribute('aria-invalid', 'true');
      if (name === 'distractors' || name === 'options') {
        this.attForm.querySelectorAll('[name^="distractor-"]').forEach(control => control.setAttribute('aria-invalid', 'true'));
      }
      if (name === 'options') this.attForm.elements.namedItem('correctAnswer')?.setAttribute('aria-invalid', 'true');
    });
  }

  /** Limpia errores visuales y del formulario. */
  opClearErrors() {
    this.attForm.querySelectorAll('[aria-invalid="true"]').forEach(field => field.removeAttribute('aria-invalid'));
    this.attForm.querySelectorAll('[data-error-for]').forEach(label => { label.textContent = ''; });
  }
}