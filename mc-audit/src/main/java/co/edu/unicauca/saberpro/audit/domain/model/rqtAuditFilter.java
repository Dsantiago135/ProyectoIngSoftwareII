package co.edu.unicauca.saberpro.audit.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Filtros opcionales de consulta de auditoría. */
public record rqtAuditFilter(UUID actorId, String entityType, UUID entityId, Instant from, Instant to) { }