package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.clsAuditEvent;

public interface IEventPublisherPort {
    void opSendAudit(clsAuditEvent prmEvent);
}