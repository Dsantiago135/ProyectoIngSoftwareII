package co.edu.unicauca.saberpro.audit.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Registro inmutable de auditoría. */
public record clsAuditRecord(UUID id, UUID eventId, UUID actorId, String action,
                             String entityType, UUID entityId, String detail,
                             String sourceService, Instant occurredAt, Instant recordedAt) {
    /** Crea el registro y le asigna su identificador de auditoría y hora de recepción. */
    public static clsAuditRecord opCreate(UUID prmEventId, UUID prmActorId, String prmAction,
                                          String prmEntityType, UUID prmEntityId, String prmDetail,
                                          String prmSourceService, Instant prmOccurredAt) {
        if (prmEventId == null || prmAction == null || prmEntityType == null || prmEntityId == null
                || prmSourceService == null || prmOccurredAt == null) {
            throw new IllegalArgumentException("El evento de auditoría no contiene todos los campos obligatorios");
        }
        return new clsAuditRecord(UUID.randomUUID(), prmEventId, prmActorId, prmAction, prmEntityType,
                prmEntityId, prmDetail == null ? "{}" : prmDetail, prmSourceService, prmOccurredAt, Instant.now());
    }
}