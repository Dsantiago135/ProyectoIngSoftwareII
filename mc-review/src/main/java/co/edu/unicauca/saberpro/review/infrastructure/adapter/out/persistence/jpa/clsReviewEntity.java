package co.edu.unicauca.saberpro.review.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.review.domain.model.enmDecision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"questionId", "reviewerId"}))
public class clsReviewEntity {
    @Id private UUID id;
    @Column(nullable = false) private UUID questionId;
    @Column(nullable = false) private UUID reviewerId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private enmDecision decision;
    @Column(nullable = false, columnDefinition = "text") private String justification;
    @Column(nullable = false) private Instant createdAt;
    protected clsReviewEntity() { }
    public clsReviewEntity(UUID prmId, UUID prmQuestionId, UUID prmReviewerId, enmDecision prmDecision,
                           String prmJustification, Instant prmCreatedAt) {
        id = prmId; questionId = prmQuestionId; reviewerId = prmReviewerId; decision = prmDecision;
        justification = prmJustification; createdAt = prmCreatedAt;
    }
    public UUID getId() { return id; }
    public UUID getQuestionId() { return questionId; }
    public UUID getReviewerId() { return reviewerId; }
    public enmDecision getDecision() { return decision; }
    public String getJustification() { return justification; }
    public Instant getCreatedAt() { return createdAt; }
}