/** Crea un nodo HTML y asigna atributos/texto sin interpretar contenido como HTML. */
export function opElement(prmTag, prmOptions = {}, ...prmChildren) {
  const element = document.createElement(prmTag);
  Object.entries(prmOptions).forEach(([key, value]) => {
    if (value === undefined || value === null) return;
    if (key === 'text') element.textContent = String(value);
    else if (key === 'className') element.className = value;
    else if (key === 'dataset') Object.assign(element.dataset, value);
    else if (key.startsWith('aria') || key.startsWith('data-')) element.setAttribute(key.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`), String(value));
    else if (key === 'htmlFor') element.htmlFor = value;
    else if (key === 'value') element.value = value;
    else if (key in element && !key.startsWith('on')) element[key] = value;
    else element.setAttribute(key, String(value));
  });
  prmChildren.flat(Infinity).forEach(child => {
    if (child === undefined || child === null) return;
    element.append(child instanceof Node ? child : document.createTextNode(String(child)));
  });
  return element;
}

/** Clona la mascota SVG integrada en el documento y asigna una expresión accesible. */
export function opMascot(prmExpression = 'happy', prmLabel = 'Mascota Saber Pro') {
  const template = document.querySelector('#mascot-template');
  const mascot = template.content.firstElementChild.cloneNode(true);
  mascot.classList.remove('mascot-happy', 'mascot-sad', 'mascot-thinking', 'mascot-surprised');
  mascot.classList.add(`mascot-${prmExpression}`);
  mascot.setAttribute('aria-label', prmLabel);
  return mascot;
}

/** Convierte un valor a texto seguro para las celdas y diálogos. */
export function opText(prmValue, prmFallback = '—') {
  if (prmValue === undefined || prmValue === null || prmValue === '') return prmFallback;
  return String(prmValue);
}

/** Genera iniciales visuales sin depender del contenido como marcado. */
export function opInitials(prmName) {
  return String(prmName || '?').trim().split(/\s+/).slice(0, 2).map(part => part[0] || '').join('').toUpperCase();
}

/** Crea un icono SVG geométrico desde una ruta conocida, sin analizar HTML. */
export function opIcon(prmName) {
  const paths = {
    users: 'M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2m12-13a4 4 0 1 1-8 0 4 4 0 0 1 8 0Zm2 5h2a4 4 0 0 1 4 4v2m-4-17a4 4 0 0 1 0 8',
    question: 'M12 18h.01M9.1 9a3 3 0 1 1 5.8 1c-.7 1-2.9 1.5-2.9 3.5M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
    review: 'm9 12 2 2 4-4m6 2a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
    audit: 'M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01',
    quiz: 'M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01'
  };
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.setAttribute('viewBox', '0 0 24 24');
  svg.setAttribute('width', '24');
  svg.setAttribute('height', '24');
  svg.setAttribute('fill', 'none');
  svg.setAttribute('stroke', 'currentColor');
  svg.setAttribute('stroke-width', '1.8');
  svg.setAttribute('stroke-linecap', 'round');
  svg.setAttribute('stroke-linejoin', 'round');
  svg.setAttribute('aria-hidden', 'true');
  const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
  path.setAttribute('d', paths[prmName] || paths.question);
  svg.append(path);
  return svg;
}