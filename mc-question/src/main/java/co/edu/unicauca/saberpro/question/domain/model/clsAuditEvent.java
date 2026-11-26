package co.edu.unicauca.saberpro.question.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record clsAuditEvent(UUID eventId, String eventType, Instant occurredAt,
                            String sourceService, UUID actorId, String entityType,
                            UUID entityId, Map<String, Object> detail) {
}