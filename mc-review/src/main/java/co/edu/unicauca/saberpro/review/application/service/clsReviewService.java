package co.edu.unicauca.saberpro.review.application.service;

import co.edu.unicauca.saberpro.review.domain.exception.clsReviewException;
import co.edu.unicauca.saberpro.review.domain.model.clsQuestionSummary;
import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.model.enmDecision;
import co.edu.unicauca.saberpro.review.domain.model.enmQuestionStatus;
import co.edu.unicauca.saberpro.review.domain.ports.inbound.IReviewUseCase;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IQuestionClientPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IReviewRepositoryPort;
import java.util.List;
import java.util.UUID;

public class clsReviewService implements IReviewUseCase {
    private final IReviewRepositoryPort attRepository;
    private final IQuestionClientPort attQuestionClient;
    private final IEventPublisherPort attEvents;

    public clsReviewService(IReviewRepositoryPort prmRepository, IQuestionClientPort prmQuestionClient,
                            IEventPublisherPort prmEvents) {
        attRepository = prmRepository;
        attQuestionClient = prmQuestionClient;
        attEvents = prmEvents;
    }

    @Override
    public clsReview opCreate(rqtReview prmRequest, UUID prmReviewerId) {
        if (prmRequest == null) throw new clsReviewException(400, "La revisión es obligatoria");
        clsQuestionSummary question = opGetQuestion(prmRequest.questionId());
        if (question.authorId().equals(prmReviewerId)) throw new clsReviewException(403, "El autor no puede revisar su pregunta");
        if (question.status() != enmQuestionStatus.PENDIENTE_REVISION
                && question.status() != enmQuestionStatus.EN_REVISION) {
            throw new clsReviewException(409, "La pregunta no está disponible para revisión");
        }
        if (attRepository.opExistsByQuestionIdAndReviewerId(prmRequest.questionId(), prmReviewerId)) {
            throw new clsReviewException(409, "El revisor ya calificó esta pregunta");
        }
        if (attRepository.opFindByQuestionId(prmRequest.questionId()).size() >= 2) {
            throw new clsReviewException(409, "La pregunta ya tiene sus dos revisiones requeridas");
        }
        clsReview review;
        try {
            review = clsReview.opCreate(prmRequest.questionId(), prmReviewerId,
                    prmRequest.decision(), prmRequest.justification());
        } catch (IllegalArgumentException exception) {
            throw new clsReviewException(400, exception.getMessage());
        }
        try {
            review = attRepository.opSave(review);
        } catch (IllegalStateException exception) {
            throw new clsReviewException(409, "El revisor ya registró una decisión para esta pregunta");
        }
        attEvents.opSendAudit("review.created", prmReviewerId, review);

        List<clsReview> reviews = attRepository.opFindByQuestionId(prmRequest.questionId());
        try {
            if (reviews.size() == 1) {
                attQuestionClient.opChangeStatus(question.id(), enmQuestionStatus.EN_REVISION);
            } else if (reviews.size() == 2) {
                enmQuestionStatus result = reviews.stream().allMatch(item -> item.decision() == enmDecision.APPROVED)
                        ? enmQuestionStatus.APROBADA : enmQuestionStatus.RECHAZADA;
                attQuestionClient.opChangeStatus(question.id(), result);
                attEvents.opSendEmail(result == enmQuestionStatus.APROBADA
                                ? "question.approved" : "question.rejected",
                        prmReviewerId, question.authorId(), review);
            }
        } catch (RuntimeException exception) {
            throw new clsReviewException(502, "No fue posible sincronizar el estado con MCQuestion; la revisión quedó guardada");
        }
        return review;
    }

    @Override
    public clsReview opFindById(UUID prmId, UUID prmActorId, String prmActorRole) {
        clsReview review = attRepository.opFindById(prmId)
                .orElseThrow(() -> new clsReviewException(404, "No existe la revisión solicitada"));
        opAuthorizeQuestionAccess(review.questionId(), prmActorId, prmActorRole);
        return review;
    }

    @Override
    public List<clsReview> opFindByQuestionId(UUID prmQuestionId, UUID prmActorId, String prmActorRole) {
        opAuthorizeQuestionAccess(prmQuestionId, prmActorId, prmActorRole);
        return attRepository.opFindByQuestionId(prmQuestionId);
    }

    private void opAuthorizeQuestionAccess(UUID prmQuestionId, UUID prmActorId, String prmActorRole) {
        if ("ADMIN".equals(prmActorRole) || "REVIEWER".equals(prmActorRole)) return;
        clsQuestionSummary question = opGetQuestion(prmQuestionId);
        if (!"AUTHOR".equals(prmActorRole) || !question.authorId().equals(prmActorId)) {
            throw new clsReviewException(403, "No tiene permiso para consultar esta revisión");
        }
    }

    private clsQuestionSummary opGetQuestion(UUID prmQuestionId) {
        try {
            return attQuestionClient.opGetQuestion(prmQuestionId);
        } catch (RuntimeException exception) {
            if (exception instanceof clsReviewException reviewException) throw reviewException;
            throw new clsReviewException(502, "No fue posible consultar MCQuestion");
        }
    }
}