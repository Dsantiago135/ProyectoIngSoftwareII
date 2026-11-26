package co.edu.unicauca.saberpro.question.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;

public record clsQuestionDraft(
        enmContextType contextType,
        String context,
        String mediaUrl,
        String directQuestion,
        List<String> distractors,
        String correctAnswer,
        String justification,
        String bibliography,
        String competence,
        String topic,
        String subtopic,
        enmDifficulty difficulty) {

    public clsQuestionDraft {
        distractors = distractors == null ? null : Collections.unmodifiableList(new ArrayList<>(distractors));
    }
}