/** Estados del ciclo de vida de una pregunta. */
export const enmQuestionStatus = Object.freeze({
  BORRADOR: 'BORRADOR',
  PENDIENTE_REVISION: 'PENDIENTE_REVISION',
  EN_REVISION: 'EN_REVISION',
  APROBADA: 'APROBADA',
  RECHAZADA: 'RECHAZADA',
  PUBLICADA: 'PUBLICADA',
  ARCHIVADA: 'ARCHIVADA'
});