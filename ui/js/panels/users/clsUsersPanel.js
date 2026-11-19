import { clsPanel } from '../clsPanel.js';
import { clsUsersService } from './clsUsersService.js';
import { clsUserForm } from './clsUserForm.js';
import { clsTable } from '../../ui/clsTable.js';
import { clsFilterBar } from '../../ui/clsFilterBar.js';
import { opElement } from '../../ui/opDom.js';

/** Panel ADMIN para consultar, crear, editar y desactivar usuarios. */
export class clsUsersPanel extends clsPanel {
  /** Inicializa el servicio y formulario propios del panel. */
  constructor(prmApi, prmToast) {
    super('Usuarios', 'Administra cuentas, roles y acceso al sistema.', prmToast);
    this.attService = new clsUsersService(prmApi);
    this.attUserForm = new clsUserForm(prmToast);
    this.attUsers = [];
  }

  /** Monta encabezado, filtro local, tabla y acción de creación. */
  async opMount(prmRoot) {
    this.attRoot = prmRoot;
    const stack = opElement('div', { className: 'panel-stack users-panel' });
    const heading = this.opCreateHeading('Administración');
    const create = opElement('button', { className: 'btn btn-primary', type: 'button', text: 'Crear usuario' });
    create.addEventListener('click', () => this.attUserForm.opOpen(null, request => this.opCreate(request)));
    heading.append(create);
    stack.append(heading);
    const filters = new clsFilterBar('Filtrar usuarios');
    const search = opElement('input', { id: 'users-search', className: 'field-control', type: 'search', placeholder: 'Buscar usuario o correo', 'aria-label': 'Buscar usuario o correo' });
    filters.opAdd(opElement('label', { className: 'field', htmlFor: 'users-search' }, opElement('span', { className: 'field-label', text: 'Búsqueda' }), search));
    const roleFilter = this.opRoleFilter();
    filters.opAdd(roleFilter.label);
    const refresh = opElement('button', { className: 'btn btn-secondary', type: 'button', text: 'Actualizar lista' });
    filters.opAdd(refresh);
    stack.append(filters.opGetElement());
    this.attTable = new clsTable([
      { key: 'username', label: 'Usuario' },
      { key: 'email', label: 'Correo', render: user => opElement('span', { className: 'users-panel-email', text: user.email }) },
      { key: 'role', label: 'Rol', render: user => opElement('span', { className: 'badge users-panel-role', text: user.role }) },
      { key: 'active', label: 'Estado', render: user => this.opStatusControl(user) },
      { key: 'actions', label: 'Acciones', render: user => this.opActions(user) }
    ], 'Usuarios registrados');
    const tableSection = opElement('section', { className: 'card table-scroll', 'aria-label': 'Tabla de usuarios' });
    tableSection.append(this.attTable.opGetElement());
    this.attEmpty = this.opCreateEmpty('Sin usuarios', 'No hay cuentas que coincidan con los filtros.');
    stack.append(tableSection, this.attEmpty);
    search.addEventListener('input', () => this.opRenderRows(search.value, roleFilter.select.value));
    roleFilter.select.addEventListener('change', () => this.opRenderRows(search.value, roleFilter.select.value));
    refresh.addEventListener('click', () => this.opLoad());
    prmRoot.append(stack);
    await this.opLoad();
  }

  /** Obtiene la lista desde MCUser y presenta errores en una notificación. */
  async opLoad() {
    try { this.attUsers = await this.attService.opFindAll(); this.opRenderRows(); }
    catch (error) { this.attToast.opShow(error.message, 'error'); }
  }

  /** Aplica filtros locales y actualiza la tabla. */
  opRenderRows(prmSearch = '', prmRole = '') {
    const query = prmSearch.trim().toLocaleLowerCase('es');
    const users = this.attUsers.filter(user => (!prmRole || user.role === prmRole)
      && (!query || `${user.username} ${user.email}`.toLocaleLowerCase('es').includes(query)));
    this.attTable.opRender(users);
    this.attEmpty.hidden = users.length > 0;
  }

  /** Crea un filtro por rol, incluyendo la opción sin filtro. */
  opRoleFilter() {
    const select = opElement('select', { id: 'users-role-filter', className: 'field-control' });
    [['', 'Todos los roles'], ['ADMIN', 'ADMIN'], ['AUTHOR', 'AUTHOR'], ['REVIEWER', 'REVIEWER'], ['STUDENT', 'STUDENT'], ['TEACHER', 'TEACHER']]
      .forEach(([value, label]) => select.append(opElement('option', { value, text: label })));
    return { select, label: opElement('label', { className: 'field', htmlFor: 'users-role-filter' }, opElement('span', { className: 'field-label', text: 'Rol' }), select) };
  }

  /** Crea el control accesible de estado; no se reactiva una cuenta desactivada. */
  opStatusControl(prmUser) {
    const button = opElement('button', { className: 'switch', type: 'button', role: 'switch', 'aria-checked': String(prmUser.active), 'aria-label': `Cuenta ${prmUser.username}: ${prmUser.active ? 'activa' : 'inactiva'}`, disabled: !prmUser.active });
    const label = opElement('span', { className: `badge ${prmUser.active ? 'badge-activo' : 'badge-inactivo'}`, text: prmUser.active ? 'Activo' : 'Inactivo' });
    button.addEventListener('click', () => this.opDeactivate(prmUser));
    return opElement('span', { className: 'users-panel-switch-label' }, button, label);
  }

  /** Añade acciones de edición y desactivación para la fila. */
  opActions(prmUser) {
    const actions = opElement('div', { className: 'tbl-actions' });
    const edit = opElement('button', { className: 'btn btn-secondary btn-small', type: 'button', text: 'Editar' });
    edit.addEventListener('click', () => this.attUserForm.opOpen(prmUser, request => this.opUpdate(prmUser.id, request)));
    actions.append(edit);
    if (prmUser.active) {
      const deactivate = opElement('button', { className: 'btn btn-danger btn-small', type: 'button', text: 'Desactivar' });
      deactivate.addEventListener('click', () => this.opDeactivate(prmUser));
      actions.append(deactivate);
    }
    return actions;
  }

  /** Crea la cuenta y recarga la lista. */
  async opCreate(prmRequest) { await this.attService.opCreate(prmRequest); this.attToast.opShow('Usuario creado.'); await this.opLoad(); }

  /** Actualiza la cuenta y recarga la lista. */
  async opUpdate(prmId, prmRequest) { await this.attService.opUpdate(prmId, prmRequest); this.attToast.opShow('Cambios guardados.'); await this.opLoad(); }

  /** Desactiva una cuenta tras confirmación explícita. */
  async opDeactivate(prmUser) {
    if (!window.confirm(`¿Desactivar la cuenta de ${prmUser.username}?`)) return;
    try { await this.attService.opDeactivate(prmUser.id); this.attToast.opShow('Cuenta desactivada.'); await this.opLoad(); }
    catch (error) { this.attToast.opShow(error.message, 'error'); }
  }
}