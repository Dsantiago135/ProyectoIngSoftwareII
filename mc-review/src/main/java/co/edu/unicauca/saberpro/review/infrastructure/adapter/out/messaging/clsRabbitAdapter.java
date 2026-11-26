package co.edu.unicauca.saberpro.review.infrastructure.adapter.out.messaging;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IEventPublisherPort;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Publica eventos de revisión y aviso al autor. */
@Component
public class clsRabbitAdapter implements IEventPublisherPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(clsRabbitAdapter.class);
    private final RabbitTemplate attRabbitTemplate;
    private final ObjectMapper attMapper;
    private final String attExchange;
    public clsRabbitAdapter(RabbitTemplate prmRabbitTemplate, ObjectMapper prmMapper,
                            @Value("${messaging.exchange:app.events}") String prmExchange) {
        attRabbitTemplate = prmRabbitTemplate; attMapper = prmMapper; attExchange = prmExchange;
    }

    @Override
    public void opSendAudit(String prmEventType, UUID prmActorId, clsReview prmReview) {
        opPublish("auditable." + prmEventType, Map.of("eventId", UUID.randomUUID(), "eventType", prmEventType,
                "occurredAt", Instant.now(), "sourceService", "MCReview", "actorId", prmActorId,
                "entityType", "REVIEW", "entityId", prmReview.id(), "detail", Map.of(
                        "questionId", prmReview.questionId(), "decision", prmReview.decision(),
                        "justification", prmReview.justification())));
    }

    @Override
    public void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, clsReview prmReview) {
        opPublish("auditable.aviso." + prmEventType, Map.ofEntries(
                Map.entry("eventId", UUID.randomUUID()), Map.entry("eventType", prmEventType),
                Map.entry("occurredAt", Instant.now()), Map.entry("sourceService", "MCReview"),
                Map.entry("actorId", prmActorId), Map.entry("entityType", "QUESTION"),
                Map.entry("entityId", prmReview.questionId()), Map.entry("detail", Map.of()),
                Map.entry("recipient", Map.of("userId", prmRecipientId)),
                Map.entry("template", prmEventType), Map.entry("variables", Map.of("decision", prmReview.decision().name(),
                        "justification", prmReview.justification()))));
    }

    private void opPublish(String prmRoutingKey, Map<String, Object> prmEvent) {
        try {
            attRabbitTemplate.convertAndSend(attExchange, prmRoutingKey, attMapper.writeValueAsString(prmEvent));
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible publicar el evento {}", prmEvent.get("eventType"), exception);
        }
    }
}