package co.edu.unicauca.saberpro.question.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.unicauca.saberpro.question.application.service.clsQuestionService;
import co.edu.unicauca.saberpro.question.domain.model.clsAuditEvent;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IQuestionRepositoryPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class clsQuestionServiceTest {

    @Test
    void createsQuestionPersistsAndPublishesAuditEvent() {
        UUID authorId = UUID.randomUUID();
        Map<UUID, clsQuestion> storedQuestions = new HashMap<>();
        List<clsAuditEvent> events = new java.util.ArrayList<>();
        IQuestionRepositoryPort repository = new IQuestionRepositoryPort() {
            @Override
            public clsQuestion opSave(clsQuestion prmQuestion) {
                storedQuestions.put(prmQuestion.getId(), prmQuestion);
                return prmQuestion;
            }

            @Override
            public Optional<clsQuestion> opFindById(UUID prmId) {
                return Optional.ofNullable(storedQuestions.get(prmId));
            }
        };
        IEventPublisherPort eventPublisher = events::add;
        clsQuestionService service = new clsQuestionService(repository, eventPublisher);

        clsQuestion created = service.opCreate(validDraft(), authorId);

        assertEquals(enmQuestionStatus.BORRADOR, created.getStatus());
        assertEquals(created, storedQuestions.get(created.getId()));
        assertEquals("question.created", events.getFirst().eventType());
    }

    @Test
    void submitsQuestionAndPublishesStatusChange() {
        UUID authorId = UUID.randomUUID();
        Map<UUID, clsQuestion> storedQuestions = new HashMap<>();
        List<clsAuditEvent> events = new java.util.ArrayList<>();
        IQuestionRepositoryPort repository = new IQuestionRepositoryPort() {
            @Override
            public clsQuestion opSave(clsQuestion prmQuestion) {
                storedQuestions.put(prmQuestion.getId(), prmQuestion);
                return prmQuestion;
            }

            @Override
            public Optional<clsQuestion> opFindById(UUID prmId) {
                return Optional.ofNullable(storedQuestions.get(prmId));
            }
        };
        clsQuestionService service = new clsQuestionService(repository, events::add);
        clsQuestion created = service.opCreate(validDraft(), authorId);

        clsQuestion submitted = service.opSubmit(created.getId(), authorId);

        assertEquals(enmQuestionStatus.PENDIENTE_REVISION, submitted.getStatus());
        assertEquals("question.status-changed", events.getLast().eventType());
    }

    private static clsQuestionDraft validDraft() {
        return new clsQuestionDraft(enmContextType.MULTIPLE_CHOICE, "Contexto", null,
                "¿Cuál es la respuesta?", List.of("A", "B", "C", "D"), "Correcta",
                "Justificación", null, "Competencia", "Tema", "Subtema", enmDifficulty.MEDIA);
    }
}