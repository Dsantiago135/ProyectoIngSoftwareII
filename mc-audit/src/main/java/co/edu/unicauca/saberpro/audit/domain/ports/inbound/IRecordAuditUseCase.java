package co.edu.unicauca.saberpro.audit.domain.ports.inbound;

import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditEvent;

/** Puerto de entrada para registrar eventos recibidos desde RabbitMQ. */
public interface IRecordAuditUseCase {
    void opRecord(rqtAuditEvent prmEvent);
}