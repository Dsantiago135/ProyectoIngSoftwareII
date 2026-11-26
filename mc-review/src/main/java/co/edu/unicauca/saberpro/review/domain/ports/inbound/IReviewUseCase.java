package co.edu.unicauca.saberpro.review.domain.ports.inbound;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.model.enmDecision;
import java.util.List;
import java.util.UUID;

/** Puerto de entrada para registrar y consultar revisiones. */
public interface IReviewUseCase {
    record rqtReview(UUID questionId, enmDecision decision, String justification) { }
    clsReview opCreate(rqtReview prmRequest, UUID prmReviewerId);
    clsReview opFindById(UUID prmId, UUID prmActorId, String prmActorRole);
    List<clsReview> opFindByQuestionId(UUID prmQuestionId, UUID prmActorId, String prmActorRole);
}