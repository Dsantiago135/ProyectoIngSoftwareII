package co.edu.unicauca.saberpro.messaging.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Envelope JSON para los eventos que requieren notificación. */
public record rqtEmailEvent(UUID eventId, String eventType, Instant occurredAt, String sourceService,
                            UUID actorId, rqtRecipient recipient, String template,
                            Map<String, Object> variables) {
    /** Destinatario con correo opcional y referencia interna al usuario. */
    public record rqtRecipient(UUID userId, String email) { }
}