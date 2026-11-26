package co.edu.unicauca.saberpro.audit.infrastructure.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_records", uniqueConstraints = @UniqueConstraint(columnNames = "eventId"))
public class clsAuditEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private UUID eventId;
    private UUID actorId;
    @Column(nullable = false, length = 120) private String action;
    @Column(nullable = false, length = 80) private String entityType;
    @Column(nullable = false) private UUID entityId;
    @Column(nullable = false, columnDefinition = "text") private String detail;
    @Column(nullable = false, length = 80) private String sourceService;
    @Column(nullable = false) private Instant occurredAt;
    @Column(nullable = false) private Instant recordedAt;
    protected clsAuditEntity() { }
    public clsAuditEntity(UUID prmId, UUID prmEventId, UUID prmActorId, String prmAction,
                          String prmEntityType, UUID prmEntityId, String prmDetail,
                          String prmSourceService, Instant prmOccurredAt, Instant prmRecordedAt) {
        id = prmId; eventId = prmEventId; actorId = prmActorId; action = prmAction;
        entityType = prmEntityType; entityId = prmEntityId; detail = prmDetail;
        sourceService = prmSourceService; occurredAt = prmOccurredAt; recordedAt = prmRecordedAt;
    }
    public UUID getId() { return id; }
    public UUID getEventId() { return eventId; }
    public UUID getActorId() { return actorId; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getDetail() { return detail; }
    public String getSourceService() { return sourceService; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getRecordedAt() { return recordedAt; }
}