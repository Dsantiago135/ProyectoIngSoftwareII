package co.edu.unicauca.saberpro.review.domain.model;

import java.util.UUID;

public record clsQuestionSummary(UUID id, UUID authorId, enmQuestionStatus status) { }