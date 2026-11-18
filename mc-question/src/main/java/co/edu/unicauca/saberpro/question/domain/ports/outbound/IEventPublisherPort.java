package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.clsAuditEvent;

/** Puerto de salida para publicar eventos auditables. */
public interface IEventPublisherPort {
    void opSendAudit(clsAuditEvent prmEvent);
}