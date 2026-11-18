package co.edu.unicauca.saberpro.review.infrastructure.adapter.out.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data para revisiones ordenadas por fecha. */
public interface SpringDataReviewRepository extends JpaRepository<clsReviewEntity, UUID> {
    List<clsReviewEntity> findAllByQuestionIdOrderByCreatedAtAsc(UUID questionId);
    boolean existsByQuestionIdAndReviewerId(UUID questionId, UUID reviewerId);
}