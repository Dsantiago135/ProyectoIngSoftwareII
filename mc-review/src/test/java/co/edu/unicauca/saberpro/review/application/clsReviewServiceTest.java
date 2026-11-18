package co.edu.unicauca.saberpro.review.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unicauca.saberpro.review.application.service.clsReviewService;
import co.edu.unicauca.saberpro.review.domain.exception.clsReviewException;
import co.edu.unicauca.saberpro.review.domain.model.clsQuestionSummary;
import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.model.enmDecision;
import co.edu.unicauca.saberpro.review.domain.model.enmQuestionStatus;
import co.edu.unicauca.saberpro.review.domain.ports.inbound.IReviewUseCase;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IQuestionClientPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IReviewRepositoryPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class clsReviewServiceTest {
    @Test
    void firstReviewMovesQuestionToInReviewAndSecondApprovalCompletes() {
        Fixture fixture = new Fixture();
        clsReview first = fixture.service.opCreate(request(fixture, enmDecision.APPROVED), UUID.randomUUID());
        assertEquals(enmQuestionStatus.EN_REVISION, fixture.questionClient.status);
        clsReview second = fixture.service.opCreate(request(fixture, enmDecision.APPROVED), UUID.randomUUID());

        assertEquals(enmQuestionStatus.APROBADA, fixture.questionClient.status);
        assertEquals(1, fixture.events.emails);
        assertEquals(first.questionId(), second.questionId());
    }

    @Test
    void anyRejectionAfterTwoReviewsRejectsQuestion() {
        Fixture fixture = new Fixture();
        fixture.service.opCreate(request(fixture, enmDecision.APPROVED), UUID.randomUUID());
        fixture.service.opCreate(request(fixture, enmDecision.REJECTED), UUID.randomUUID());

        assertEquals(enmQuestionStatus.RECHAZADA, fixture.questionClient.status);
    }

    @Test
    void authorCannotReviewOwnQuestion() {
        Fixture fixture = new Fixture();
        clsReviewException exception = assertThrows(clsReviewException.class,
                () -> fixture.service.opCreate(request(fixture, enmDecision.APPROVED), fixture.authorId));
        assertEquals(403, exception.getStatus());
    }

    @Test
    void reviewerCannotSubmitTwice() {
        Fixture fixture = new Fixture();
        UUID reviewerId = UUID.randomUUID();
        fixture.service.opCreate(request(fixture, enmDecision.APPROVED), reviewerId);
        clsReviewException exception = assertThrows(clsReviewException.class,
                () -> fixture.service.opCreate(request(fixture, enmDecision.REJECTED), reviewerId));
        assertEquals(409, exception.getStatus());
    }

    private IReviewUseCase.rqtReview request(Fixture prmFixture, enmDecision prmDecision) {
        return new IReviewUseCase.rqtReview(prmFixture.questionId, prmDecision, "Revisión detallada y suficiente");
    }

    private static final class Fixture {
        private final UUID questionId = UUID.randomUUID();
        private final UUID authorId = UUID.randomUUID();
        private final ReviewMemory repository = new ReviewMemory();
        private final QuestionMemory questionClient = new QuestionMemory(questionId, authorId);
        private final Events events = new Events();
        private final clsReviewService service = new clsReviewService(repository, questionClient, events);
    }

    private static final class ReviewMemory implements IReviewRepositoryPort {
        private final Map<UUID, clsReview> reviews = new HashMap<>();
        @Override public clsReview opSave(clsReview prmReview) { reviews.put(prmReview.id(), prmReview); return prmReview; }
        @Override public Optional<clsReview> opFindById(UUID prmId) { return Optional.ofNullable(reviews.get(prmId)); }
        @Override public List<clsReview> opFindByQuestionId(UUID prmQuestionId) {
            return reviews.values().stream().filter(review -> review.questionId().equals(prmQuestionId)).toList();
        }
        @Override public boolean opExistsByQuestionIdAndReviewerId(UUID prmQuestionId, UUID prmReviewerId) {
            return opFindByQuestionId(prmQuestionId).stream().anyMatch(review -> review.reviewerId().equals(prmReviewerId));
        }
    }

    private static final class QuestionMemory implements IQuestionClientPort {
        private final UUID questionId;
        private final UUID authorId;
        private enmQuestionStatus status = enmQuestionStatus.PENDIENTE_REVISION;
        private QuestionMemory(UUID prmQuestionId, UUID prmAuthorId) { questionId = prmQuestionId; authorId = prmAuthorId; }
        @Override public clsQuestionSummary opGetQuestion(UUID prmQuestionId) { return new clsQuestionSummary(questionId, authorId, status); }
        @Override public void opChangeStatus(UUID prmQuestionId, enmQuestionStatus prmStatus) { status = prmStatus; }
    }

    private static final class Events implements IEventPublisherPort {
        private int emails;
        @Override public void opSendAudit(String prmEventType, UUID prmActorId, clsReview prmReview) { }
        @Override public void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, clsReview prmReview) { emails++; }
    }
}