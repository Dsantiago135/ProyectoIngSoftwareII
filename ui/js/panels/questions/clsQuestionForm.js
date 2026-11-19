import { enmContextType } from '../../config/enmContextType.js';
import { clsDialog } from '../../ui/clsDialog.js';
import { clsForm } from '../../ui/clsForm.js';
import { opElement } from '../../ui/opDom.js';

/** Formulario de alta de pregunta con campos estructurados del dominio. */
export class clsQuestionForm {
  /** Conserva el validador del dominio y las notificaciones del módulo. */
  constructor(prmValidator, prmToast) { this.attValidator = prmValidator; this.attToast = prmToast; }

  /** Abre el formulario de creación y llama al servicio cuando es válido. */
  opOpen(prmOnSubmit) {
    const form = opElement('form', { id: 'question-form', className: 'form-grid question-form', novalidate: true });
    const contextType = this.opSelectField('contextType', 'Tipo de contexto', Object.values(enmContextType), enmContextType.MULTIPLE_CHOICE);
    const mediaUrl = this.opTextField('mediaUrl', 'URL multimedia', { type: 'url', full: true });
    mediaUrl.hidden = true;
    const options = opElement('fieldset', { className: 'question-form-options form-grid-full' });
    options.append(opElement('legend', { className: 'field-label', text: 'Cuatro distractores' }));
    for (let index = 0; index < 4; index += 1) options.append(this.opTextField(`distractor-${index}`, `Distractor ${index + 1}`, { required: true }));
    options.append(opElement('span', { className: 'field-error form-grid-full', 'data-error-for': 'distractors', 'aria-live': 'polite' }));
    options.append(opElement('span', { className: 'field-error form-grid-full', 'data-error-for': 'options', 'aria-live': 'polite' }));
    form.append(
      contextType,
      this.opTextField('context', 'Contexto', { required: true, full: true, multiline: true }),
      mediaUrl,
      this.opTextField('directQuestion', 'Pregunta directa', { required: true, full: true, multiline: true }),
      this.opTextField('correctAnswer', 'Respuesta correcta', { required: true, full: true }),
      options,
      this.opTextField('justification', 'Justificación', { required: true, full: true, multiline: true }),
      this.opTextField('bibliography', 'Bibliografía'),
      this.opTextField('competence', 'Competencia'),
      this.opTextField('topic', 'Tema'),
      this.opTextField('subtopic', 'Subtema'),
      this.opSelectField('difficulty', 'Dificultad', ['BAJA', 'MEDIA', 'ALTA'], 'MEDIA')
    );
    const save = opElement('button', { className: 'btn btn-primary', type: 'submit', text: 'Guardar borrador' });
    save.setAttribute('form', form.id);
    const cancel = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Cancelar' });
    const dialog = new clsDialog('Nueva pregunta', form, [cancel, save]);
    const helper = new clsForm(form);
    contextType.querySelector('select').addEventListener('change', event => { mediaUrl.hidden = event.target.value !== enmContextType.MULTIMEDIA; });
    cancel.addEventListener('click', () => dialog.opClose());
    form.addEventListener('submit', async event => {
      event.preventDefault();
      helper.opClearErrors();
      if (!form.reportValidity()) return;
      const request = helper.opGetValues();
      request.distractors = [0, 1, 2, 3].map(index => request[`distractor-${index}`]);
      [0, 1, 2, 3].forEach(index => delete request[`distractor-${index}`]);
      Object.keys(request).forEach(key => { if (request[key] === '') request[key] = null; });
      const errors = this.attValidator.opValidate(request);
      if (Object.keys(errors).length) { helper.opSetErrors(errors); return; }
      save.disabled = true;
      try {
        await prmOnSubmit(request);
        dialog.opClose();
      } catch (error) {
        if (error.attStatus === 400) helper.opSetErrors(error.attFieldErrors);
        this.attToast.opShow(error.message, 'error');
      } finally { save.disabled = false; }
    });
    dialog.opOpen();
    return dialog;
  }

  /** Crea un control de texto o área multilínea con etiqueta asociada. */
  opTextField(prmName, prmLabel, prmOptions = {}) {
    const id = `question-${prmName}`;
    const control = prmOptions.multiline
      ? opElement('textarea', { id, name: prmName, className: 'field-control field-control-textarea', required: prmOptions.required || false })
      : opElement('input', { id, name: prmName, className: 'field-control', type: prmOptions.type || 'text', required: prmOptions.required || false });
    return opElement('label', { className: `field${prmOptions.full ? ' form-grid-full' : ''}`, htmlFor: id },
      opElement('span', { className: 'field-label', text: prmLabel }), control,
      opElement('span', { className: 'field-error', 'data-error-for': prmName, 'aria-live': 'polite' }));
  }

  /** Crea un selector con opciones explícitas sin insertar HTML. */
  opSelectField(prmName, prmLabel, prmOptions, prmSelected) {
    const id = `question-${prmName}`;
    const select = opElement('select', { id, name: prmName, className: 'field-control', required: true });
    prmOptions.forEach(value => {
      const option = opElement('option', { value, text: value });
      option.selected = value === prmSelected;
      select.append(option);
    });
    return opElement('label', { className: 'field', htmlFor: id }, opElement('span', { className: 'field-label', text: prmLabel }), select,
      opElement('span', { className: 'field-error', 'data-error-for': prmName, 'aria-live': 'polite' }));
  }
}