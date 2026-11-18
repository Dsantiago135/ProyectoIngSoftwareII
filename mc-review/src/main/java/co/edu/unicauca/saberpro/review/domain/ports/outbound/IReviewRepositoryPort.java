package co.edu.unicauca.saberpro.review.domain.ports.outbound;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia para revisiones. */
public interface IReviewRepositoryPort {
    clsReview opSave(clsReview prmReview);
    Optional<clsReview> opFindById(UUID prmId);
    List<clsReview> opFindByQuestionId(UUID prmQuestionId);
    boolean opExistsByQuestionIdAndReviewerId(UUID prmQuestionId, UUID prmReviewerId);
}