package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.messaging;

import co.edu.unicauca.saberpro.user.domain.ports.outbound.IEventPublisherPort;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Publica eventos de usuario y avisos en el exchange compartido. */
@Component
public class clsRabbitAdapter implements IEventPublisherPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(clsRabbitAdapter.class);
    private final RabbitTemplate attRabbitTemplate;
    private final ObjectMapper attMapper;
    private final String attExchange;

    public clsRabbitAdapter(RabbitTemplate prmRabbitTemplate, ObjectMapper prmMapper,
                            @Value("${messaging.exchange:app.events}") String prmExchange) {
        attRabbitTemplate = prmRabbitTemplate;
        attMapper = prmMapper;
        attExchange = prmExchange;
    }

    @Override
    public void opSendAudit(String prmEventType, UUID prmActorId, UUID prmEntityId,
                            Map<String, Object> prmDetail) {
        opPublish("auditable." + prmEventType, Map.of("eventId", UUID.randomUUID(),
                "eventType", prmEventType, "occurredAt", Instant.now(), "sourceService", "MCUser",
                "actorId", prmActorId, "entityType", "USER", "entityId", prmEntityId, "detail", prmDetail));
    }

    @Override
    public void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, String prmEmail,
                            String prmTemplate, Map<String, Object> prmVariables) {
        Map<String, Object> recipient = prmEmail == null ? Map.of("userId", prmRecipientId)
                : Map.of("userId", prmRecipientId, "email", prmEmail);
        opPublish("auditable.aviso." + prmEventType, Map.ofEntries(
            Map.entry("eventId", UUID.randomUUID()), Map.entry("eventType", prmEventType),
            Map.entry("occurredAt", Instant.now()), Map.entry("sourceService", "MCUser"),
            Map.entry("actorId", prmActorId), Map.entry("entityType", "USER"),
            Map.entry("entityId", prmRecipientId), Map.entry("detail", Map.of()),
            Map.entry("recipient", recipient), Map.entry("template", prmTemplate),
            Map.entry("variables", prmVariables)));
    }

    /**
     * Envía en evento a una cola de RabbitMQ
     * @param prmRoutingKey Cola a enviar
     * @param prmEvent Evento a enviar
    */
    private void opPublish(String prmRoutingKey, Map<String, Object> prmEvent) {
        try {
            attRabbitTemplate.convertAndSend(attExchange, prmRoutingKey, attMapper.writeValueAsString(prmEvent));
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible publicar el evento {}", prmEvent.get("eventType"), exception);
        }
    }
}