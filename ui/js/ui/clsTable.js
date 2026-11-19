import { opElement } from './opDom.js';

/** Renderiza tablas reutilizables sin convertir datos a HTML. */
export class clsTable {
  /** Crea una tabla con las columnas declaradas. */
  constructor(prmColumns, prmCaption) {
    this.attColumns = prmColumns;
    this.attTable = opElement('table', { className: 'tbl' });
    this.attTable.append(opElement('caption', { className: 'sr-only', text: prmCaption }));
    this.attHead = opElement('thead');
    this.attBody = opElement('tbody');
    const row = opElement('tr');
    prmColumns.forEach(column => row.append(opElement('th', { scope: 'col', text: column.label })));
    this.attHead.append(row);
    this.attTable.append(this.attHead, this.attBody);
  }

  /** Sustituye el contenido de la tabla por las filas recibidas. */
  opRender(prmRows) {
    this.attBody.replaceChildren();
    prmRows.forEach(rowData => {
      const row = opElement('tr');
      this.attColumns.forEach(column => {
        const cell = opElement('td');
        const value = column.render ? column.render(rowData) : rowData[column.key];
        if (value instanceof Node) cell.append(value);
        else cell.textContent = value === undefined || value === null || value === '' ? '—' : String(value);
        row.append(cell);
      });
      this.attBody.append(row);
    });
  }

  /** Devuelve la tabla lista para insertarse en una vista. */
  opGetElement() { return this.attTable; }
}