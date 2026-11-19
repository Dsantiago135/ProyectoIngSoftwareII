package co.edu.unicauca.saberpro.question.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unicauca.saberpro.question.domain.exception.clsAuthorizationException;
import co.edu.unicauca.saberpro.question.domain.exception.clsDomainException;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class clsQuestionTest {

    private final UUID authorId = UUID.randomUUID();
    @Test
    void createsDraftAndSubmitsForReview() {
        clsQuestion question = clsQuestion.opCreate(authorId, validDraft());

        assertEquals(enmQuestionStatus.BORRADOR, question.getStatus());
        question.opSubmit(authorId);
        assertEquals(enmQuestionStatus.PENDIENTE_REVISION, question.getStatus());
    }

    @Test
    void rejectsDistractorsWithForbiddenPhraseIgnoringAccentsAndCase() {
        clsQuestionDraft draft = new clsQuestionDraft(enmContextType.MULTIPLE_CHOICE, "Contexto", null,
                "¿Cuál es la respuesta?", List.of("Todas LAS anteriores", "Opción B", "Opción C", "Opción D"),
                "Opción correcta", "Justificación", null, "Competencia", "Tema", "Subtema", enmDifficulty.MEDIA);

        assertThrows(clsDomainException.class, () -> clsQuestion.opCreate(authorId, draft));
    }

    @Test
    void rejectsDuplicateDistractorsAfterNormalization() {
        clsQuestionDraft draft = new clsQuestionDraft(enmContextType.MULTIPLE_CHOICE, "Contexto", null,
                "¿Cuál es la respuesta?", List.of("Árbol", "arbol", "Opción C", "Opción D"),
                "Opción correcta", "Justificación", null, "Competencia", "Tema", "Subtema", enmDifficulty.MEDIA);

        assertThrows(clsDomainException.class, () -> clsQuestion.opCreate(authorId, draft));
    }

    @Test
    void requiresMultimediaUrl() {
        clsQuestionDraft draft = new clsQuestionDraft(enmContextType.MULTIMEDIA, "Contexto", null,
                "¿Cuál es la respuesta?", List.of("A", "B", "C", "D"), "Correcta",
                "Justificación", null, "Competencia", "Tema", "Subtema", enmDifficulty.BAJA);

        assertThrows(clsDomainException.class, () -> clsQuestion.opCreate(authorId, draft));
    }

    @Test
    void requiresExactlyFourDistractors() {
        clsQuestionDraft draft = new clsQuestionDraft(enmContextType.MULTIPLE_CHOICE, "Contexto", null,
                "¿Cuál es la respuesta?", List.of("A", "B", "C"), "Correcta",
                "Justificación", null, "Competencia", "Tema", "Subtema", enmDifficulty.BAJA);

        assertThrows(clsDomainException.class, () -> clsQuestion.opCreate(authorId, draft));
    }

    @Test
    void onlyAuthorCanSubmit() {
        clsQuestion question = clsQuestion.opCreate(authorId, validDraft());

        assertThrows(clsAuthorizationException.class, () -> question.opSubmit(UUID.randomUUID()));
    }

    private clsQuestionDraft validDraft() {
        return new clsQuestionDraft(enmContextType.MULTIPLE_CHOICE, "Contexto de la pregunta", null,
                "¿Cuál es la respuesta correcta?", List.of("Opción A", "Opción B", "Opción C", "Opción D"),
                "Respuesta", "Justificación suficiente", "Referencia", "Lectura crítica", "Argumentación",
                "Inferencia", enmDifficulty.MEDIA);
    }
}