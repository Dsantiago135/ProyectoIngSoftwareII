import { enmRole } from '../../config/enmRole.js';
import { clsDialog } from '../../ui/clsDialog.js';
import { clsForm } from '../../ui/clsForm.js';
import { opElement } from '../../ui/opDom.js';

/** Presenta el formulario de alta o edición de un usuario ADMIN. */
export class clsUserForm {
  /** Conserva el notificador del panel. */
  constructor(prmToast) { this.attToast = prmToast; }

  /** Abre el diálogo y delega el guardado al callback del panel. */
  opOpen(prmUser, prmOnSubmit) {
    const editing = Boolean(prmUser);
    const form = opElement('form', { id: 'user-form', className: 'form-grid', novalidate: true });
    form.append(this.opTextField('username', 'Usuario', prmUser?.username || '', { required: true, readOnly: editing }));
    form.append(this.opTextField('email', 'Correo', prmUser?.email || '', { required: true, type: 'email' }));
    form.append(this.opRoleField(prmUser?.role || enmRole.AUTHOR));
    form.append(this.opTextField('password', editing ? 'Nueva contraseña (opcional)' : 'Contraseña', '', {
      required: !editing, type: 'password', minLength: 8, autoComplete: 'new-password'
    }));
    const save = opElement('button', { className: 'btn btn-primary', type: 'submit', text: editing ? 'Guardar cambios' : 'Crear usuario' });
    save.setAttribute('form', form.id);
    const cancel = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Cancelar' });
    const dialog = new clsDialog(editing ? 'Editar usuario' : 'Crear usuario', form, [cancel, save]);
    const helper = new clsForm(form);
    cancel.addEventListener('click', () => dialog.opClose());
    form.addEventListener('submit', async event => {
      event.preventDefault();
      helper.opClearErrors();
      if (!form.reportValidity()) return;
      const request = helper.opGetValues();
      if (!request.password) delete request.password;
      save.disabled = true;
      try {
        await prmOnSubmit(request);
        dialog.opClose();
      } catch (error) {
        if (error.attStatus === 409) this.attToast.opShow('El usuario ya existe.', 'error');
        else {
          if (error.attStatus === 400) helper.opSetErrors(error.attFieldErrors);
          this.attToast.opShow(error.message, 'error');
        }
      } finally { save.disabled = false; }
    });
    dialog.opOpen();
    return dialog;
  }

  /** Crea un campo de texto con etiqueta y mensaje de error accesibles. */
  opTextField(prmName, prmLabel, prmValue, prmAttributes = {}) {
    const id = `user-${prmName}`;
    const input = opElement('input', { id, name: prmName, className: 'field-control', value: prmValue, ...prmAttributes });
    return opElement('label', { className: 'field', htmlFor: id }, opElement('span', { className: 'field-label', text: prmLabel }), input,
      opElement('span', { className: 'field-error', 'data-error-for': prmName, 'aria-live': 'polite' }));
  }

  /** Crea el selector de rol desde el enum central. */
  opRoleField(prmValue) {
    const select = opElement('select', { id: 'user-role', name: 'role', className: 'field-control', required: true });
    Object.values(enmRole).forEach(role => {
      const option = opElement('option', { value: role, text: role });
      option.selected = role === prmValue;
      select.append(option);
    });
    return opElement('label', { className: 'field', htmlFor: 'user-role' }, opElement('span', { className: 'field-label', text: 'Rol' }), select,
      opElement('span', { className: 'field-error', 'data-error-for': 'role', 'aria-live': 'polite' }));
  }
}