/** Valida en cliente invariantes estructurales de una pregunta antes del envío. */
export class clsQuestionValidator {
  /** Devuelve un mapa de errores por campo sin modificar el borrador recibido. */
  opValidate(prmQuestion) {
    const errors = {};
    if (!prmQuestion.context?.trim()) errors.context = 'El contexto es obligatorio.';
    if (!prmQuestion.directQuestion?.trim()) errors.directQuestion = 'La pregunta directa es obligatoria.';
    if (!prmQuestion.correctAnswer?.trim()) errors.correctAnswer = 'La respuesta correcta es obligatoria.';
    if (!prmQuestion.justification?.trim()) errors.justification = 'La justificación es obligatoria.';
    const distractors = Array.isArray(prmQuestion.distractors) ? prmQuestion.distractors : [];
    if (distractors.length !== 4) errors.distractors = 'Ingresa exactamente cuatro distractores.';
    else if (distractors.some(value => !value?.trim())) errors.distractors = 'Los cuatro distractores son obligatorios.';
    const normalized = distractors.map(value => this.opNormalize(value || ''));
    if (new Set(normalized).size !== normalized.length) errors.distractors = 'Los distractores no pueden repetirse.';
    const options = [...distractors, prmQuestion.correctAnswer || ''];
    if (options.some(value => this.opHasForbiddenPhrase(this.opNormalize(value)))) {
      errors.options = 'No uses “Todas las anteriores” ni “Ninguna de las anteriores”.';
    }
    if (prmQuestion.contextType === 'MULTIMEDIA' && !prmQuestion.mediaUrl?.trim()) {
      errors.mediaUrl = 'La URL del recurso multimedia es obligatoria.';
    }
    if (prmQuestion.contextType !== 'MULTIMEDIA' && prmQuestion.mediaUrl?.trim()) {
      errors.mediaUrl = 'La URL multimedia solo aplica a ese tipo de pregunta.';
    }
    return errors;
  }

  /** Normaliza mayúsculas y tildes para comparar respuestas. */
  opNormalize(prmValue) {
    return String(prmValue).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('es').trim();
  }

  /** Detecta si una opción contiene las expresiones prohibidas. */
  opHasForbiddenPhrase(prmValue) {
    return prmValue.includes('todas las anteriores') || prmValue.includes('ninguna de las anteriores');
  }
}