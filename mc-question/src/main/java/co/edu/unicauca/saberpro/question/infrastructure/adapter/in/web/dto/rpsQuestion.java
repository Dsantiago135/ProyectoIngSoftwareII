package co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web.dto;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public record rpsQuestion(UUID id, UUID authorId, enmContextType contextType, String context,
                          String mediaUrl, String directQuestion, List<String> distractors,
                          String correctAnswer, String justification, String bibliography,
                          String competence, String topic, String subtopic, enmDifficulty difficulty,
                          enmQuestionStatus status, Instant createdAt, Instant updatedAt) {
}