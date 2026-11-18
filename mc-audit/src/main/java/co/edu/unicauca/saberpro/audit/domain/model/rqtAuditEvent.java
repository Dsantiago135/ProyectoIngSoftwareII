package co.edu.unicauca.saberpro.audit.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Contrato JSON compartido para los eventos auditables. */
public record rqtAuditEvent(UUID eventId, String eventType, Instant occurredAt,
                            String sourceService, UUID actorId, String entityType,
                            UUID entityId, Map<String, Object> detail) { }