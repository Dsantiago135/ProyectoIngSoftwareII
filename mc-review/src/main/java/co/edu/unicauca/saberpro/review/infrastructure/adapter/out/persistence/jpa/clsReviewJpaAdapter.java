package co.edu.unicauca.saberpro.review.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IReviewRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/** Adaptador JPA para el puerto de revisiones. */
@Repository
public class clsReviewJpaAdapter implements IReviewRepositoryPort {
    private final SpringDataReviewRepository attRepository;
    public clsReviewJpaAdapter(SpringDataReviewRepository prmRepository) { attRepository = prmRepository; }

    @Override
    public clsReview opSave(clsReview prmReview) {
        try {
            return opToDomain(attRepository.save(new clsReviewEntity(prmReview.id(), prmReview.questionId(),
                    prmReview.reviewerId(), prmReview.decision(), prmReview.justification(), prmReview.createdAt())));
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException("El revisor ya registró una decisión para esta pregunta", exception);
        }
    }
    @Override public Optional<clsReview> opFindById(UUID prmId) { return attRepository.findById(prmId).map(this::opToDomain); }
    @Override public List<clsReview> opFindByQuestionId(UUID prmQuestionId) {
        return attRepository.findAllByQuestionIdOrderByCreatedAtAsc(prmQuestionId).stream().map(this::opToDomain).toList();
    }
    @Override public boolean opExistsByQuestionIdAndReviewerId(UUID prmQuestionId, UUID prmReviewerId) {
        return attRepository.existsByQuestionIdAndReviewerId(prmQuestionId, prmReviewerId);
    }
    private clsReview opToDomain(clsReviewEntity prmEntity) {
        return new clsReview(prmEntity.getId(), prmEntity.getQuestionId(), prmEntity.getReviewerId(),
                prmEntity.getDecision(), prmEntity.getJustification(), prmEntity.getCreatedAt());
    }
}