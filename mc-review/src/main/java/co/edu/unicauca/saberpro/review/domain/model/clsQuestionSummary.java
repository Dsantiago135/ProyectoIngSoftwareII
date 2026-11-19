package co.edu.unicauca.saberpro.review.domain.model;

import java.util.UUID;

/** Datos mínimos de pregunta que MCReview obtiene de MCQuestion. */
public record clsQuestionSummary(UUID id, UUID authorId, enmQuestionStatus status) { }