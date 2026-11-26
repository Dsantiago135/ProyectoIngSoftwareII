package co.edu.unicauca.saberpro.question.infrastructure.adapter.out.messaging;

import co.edu.unicauca.saberpro.question.domain.model.clsAuditEvent;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class clsRabbitAdapter implements IEventPublisherPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(clsRabbitAdapter.class);
    private final RabbitTemplate attRabbitTemplate;
    private final ObjectMapper attObjectMapper;
    private final String attExchange;

    public clsRabbitAdapter(RabbitTemplate prmRabbitTemplate, ObjectMapper prmObjectMapper,
                            @Value("${messaging.exchange:app.events}") String prmExchange) {
        attRabbitTemplate = prmRabbitTemplate;
        attObjectMapper = prmObjectMapper;
        attExchange = prmExchange;
    }

    @Override
    public void opSendAudit(clsAuditEvent prmEvent) {
        try {
            String payload = attObjectMapper.writeValueAsString(prmEvent);
            attRabbitTemplate.convertAndSend(attExchange, "auditable." + prmEvent.eventType(), payload);
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible publicar el evento auditable {}", prmEvent.eventId(), exception);
        }
    }
}