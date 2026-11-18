package co.edu.unicauca.saberpro.question.infrastructure.adapter.out.persistence.jpa;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataQuestionRepository extends JpaRepository<clsQuestionEntity, UUID> {
}