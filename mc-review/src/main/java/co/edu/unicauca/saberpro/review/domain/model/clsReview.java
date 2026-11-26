package co.edu.unicauca.saberpro.review.domain.model;

import java.time.Instant;
import java.util.UUID;

public record clsReview(UUID id, UUID questionId, UUID reviewerId, enmDecision decision,
                        String justification, Instant createdAt) {
    public static clsReview opCreate(UUID prmQuestionId, UUID prmReviewerId,
                                     enmDecision prmDecision, String prmJustification) {
        if (prmQuestionId == null || prmReviewerId == null || prmDecision == null
                || prmJustification == null || prmJustification.isBlank()) {
            throw new IllegalArgumentException("Pregunta, revisor, decisión y justificación son obligatorios");
        }
        return new clsReview(UUID.randomUUID(), prmQuestionId, prmReviewerId, prmDecision,
                prmJustification.trim(), Instant.now());
    }
}