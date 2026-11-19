import { clsDialog } from '../../ui/clsDialog.js';
import { clsForm } from '../../ui/clsForm.js';
import { opElement } from '../../ui/opDom.js';

/** Formulario modal para registrar una decisión con justificación obligatoria. */
export class clsReviewForm {
  /** Conserva el notificador del panel. */
  constructor(prmToast) { this.attToast = prmToast; }

  /** Abre la decisión y delega el envío al servicio del panel. */
  opOpen(prmQuestionId, prmOnSubmit) {
    const form = opElement('form', { id: 'review-form', className: 'form-grid', novalidate: true });
    const decision = opElement('select', { id: 'review-decision', name: 'decision', className: 'field-control', required: true });
    [['APPROVED', 'Aprobar'], ['REJECTED', 'Rechazar']].forEach(([value, label]) => decision.append(opElement('option', { value, text: label })));
    form.append(opElement('label', { className: 'field', htmlFor: 'review-decision' }, opElement('span', { className: 'field-label', text: 'Decisión' }), decision));
    const justification = opElement('textarea', { id: 'review-justification', name: 'justification', className: 'field-control field-control-textarea', required: true, minLength: 5 });
    form.append(opElement('label', { className: 'field form-grid-full', htmlFor: 'review-justification' },
      opElement('span', { className: 'field-label', text: 'Justificación' }), justification,
      opElement('span', { className: 'field-error', 'data-error-for': 'justification', 'aria-live': 'polite' })));
    const save = opElement('button', { className: 'btn btn-primary', type: 'submit', text: 'Registrar revisión' });
    save.setAttribute('form', form.id);
    const cancel = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Cancelar' });
    const dialog = new clsDialog('Registrar revisión', form, [cancel, save]);
    const helper = new clsForm(form);
    cancel.addEventListener('click', () => dialog.opClose());
    form.addEventListener('submit', async event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      save.disabled = true;
      try {
        await prmOnSubmit({ questionId: prmQuestionId, ...helper.opGetValues() });
        dialog.opClose();
      } catch (error) {
        if (error.attStatus === 400) helper.opSetErrors(error.attFieldErrors);
        this.attToast.opShow(error.message, 'error');
      } finally { save.disabled = false; }
    });
    dialog.opOpen();
    return dialog;
  }
}