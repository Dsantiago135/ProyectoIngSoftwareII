package co.edu.unicauca.saberpro.audit.infrastructure.adapter.in.messaging;

import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditEvent;
import co.edu.unicauca.saberpro.audit.domain.exception.clsAuditException;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IRecordAuditUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Convierte mensajes JSON de RabbitMQ y los registra una sola vez. */
@Component
public class clsAuditEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(clsAuditEventListener.class);
    private final IRecordAuditUseCase attRecordUseCase;
    private final ObjectMapper attMapper;
    public clsAuditEventListener(IRecordAuditUseCase prmRecordUseCase, ObjectMapper prmMapper) {
        attRecordUseCase = prmRecordUseCase; attMapper = prmMapper;
    }

    @RabbitListener(queues = "audit.queue")
    public void opOnEvent(String prmPayload) {
        rqtAuditEvent event;
        try {
            event = attMapper.readValue(prmPayload, rqtAuditEvent.class);
        } catch (Exception exception) {
            LOGGER.error("El mensaje de auditoría no contiene JSON válido; se envía a DLQ", exception);
            throw new AmqpRejectAndDontRequeueException("Evento de auditoría inválido", exception);
        }
        if (event.eventId() == null || event.eventType() == null || event.occurredAt() == null
                || event.sourceService() == null || event.entityType() == null || event.entityId() == null) {
            throw new AmqpRejectAndDontRequeueException("Faltan campos obligatorios del evento");
        }
        try {
            attRecordUseCase.opRecord(event);
        } catch (clsAuditException exception) {
            if (exception.getStatus() == 400) {
                throw new AmqpRejectAndDontRequeueException("Evento de auditoría inválido", exception);
            }
            throw exception;
        }
    }
}