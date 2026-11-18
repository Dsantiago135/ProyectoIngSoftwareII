package co.edu.unicauca.saberpro.question.application.service;

import co.edu.unicauca.saberpro.question.domain.exception.clsQuestionNotFoundException;
import co.edu.unicauca.saberpro.question.domain.model.clsAuditEvent;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.IQuestionUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IQuestionRepositoryPort;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class clsQuestionService implements IQuestionUseCase {
    private final IQuestionRepositoryPort attRepository;
    private final IEventPublisherPort attEventPublisher;

    public clsQuestionService(IQuestionRepositoryPort prmRepository,
                              IEventPublisherPort prmEventPublisher) {
        attRepository = prmRepository;
        attEventPublisher = prmEventPublisher;
    }

    @Override
    public clsQuestion opCreate(clsQuestionDraft prmDraft, UUID prmActorId) {
        clsQuestion question = attRepository.opSave(clsQuestion.opCreate(prmActorId, prmDraft));
        opPublishEvent("question.created", prmActorId, question, Map.of("status", question.getStatus().name()));
        return question;
    }

    @Override
    public clsQuestion opFindById(UUID prmId) {
        return attRepository.opFindById(prmId)
                .orElseThrow(() -> new clsQuestionNotFoundException("No existe la pregunta solicitada"));
    }

    @Override
    public clsQuestion opSubmit(UUID prmId, UUID prmActorId) {
        clsQuestion question = opFindById(prmId);
        enmQuestionStatus previousStatus = question.getStatus();
        question.opSubmit(prmActorId);
        question = attRepository.opSave(question);
        opPublishEvent("question.status-changed", prmActorId, question,
                Map.of("from", previousStatus.name(), "to", question.getStatus().name()));
        return question;
    }

    @Override
    public clsQuestion opChangeStatus(UUID prmId, enmQuestionStatus prmStatus) {
        clsQuestion question = opFindById(prmId);
        enmQuestionStatus previousStatus = question.getStatus();
        question.opChangeStatus(prmStatus);
        question = attRepository.opSave(question);
        opPublishEvent("question.status-changed", null, question,
                Map.of("from", previousStatus.name(), "to", question.getStatus().name()));
        return question;
    }

    private void opPublishEvent(String prmType, UUID prmActorId, clsQuestion prmQuestion,
                                Map<String, Object> prmDetail) {
        attEventPublisher.opSendAudit(new clsAuditEvent(UUID.randomUUID(), prmType, Instant.now(),
                "MCQuestion", prmActorId, "QUESTION", prmQuestion.getId(), prmDetail));
    }
}