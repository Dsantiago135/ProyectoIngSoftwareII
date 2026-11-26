package co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web.dto;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;
import java.util.List;

public record rqtQuestion(enmContextType contextType, String context, String mediaUrl,
                          String directQuestion, List<String> distractors, String correctAnswer,
                          String justification, String bibliography, String competence,
                          String topic, String subtopic, enmDifficulty difficulty) {
}