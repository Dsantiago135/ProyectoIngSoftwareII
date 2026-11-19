import { opElement, opIcon } from '../ui/opDom.js';

/** Construye la navegación de paneles visible para el rol de la sesión. */
export class clsMenu {
  /** Guarda el elemento de menú, configuración de roles y callback de navegación. */
  constructor(prmRoot, prmItems, prmNavigate) {
    this.attRoot = prmRoot;
    this.attItems = prmItems;
    this.attNavigate = prmNavigate;
  }

  /** Dibuja únicamente los destinos autorizados para la presentación del rol. */
  opRender(prmRole) {
    this.attRoot.replaceChildren();
    this.attItems.filter(item => item.roles.includes(prmRole)).forEach(item => {
      const listItem = opElement('li');
      const link = opElement('a', { className: 'menu-link', href: `#/${item.route}` });
      link.append(opElement('span', { className: 'menu-link-icon' }, opIcon(item.icon)));
      link.append(opElement('span', { text: item.label }));
      link.addEventListener('click', event => {
        event.preventDefault();
        this.attNavigate(item.route);
      });
      listItem.append(link);
      this.attRoot.append(listItem);
    });
  }

  /** Marca el enlace actual para lectores de pantalla y estilos. */
  opSetCurrent(prmRoute) {
    this.attRoot.querySelectorAll('a').forEach(link => {
      const current = link.getAttribute('href') === `#/${prmRoute}`;
      if (current) link.setAttribute('aria-current', 'page');
      else link.removeAttribute('aria-current');
    });
  }
}