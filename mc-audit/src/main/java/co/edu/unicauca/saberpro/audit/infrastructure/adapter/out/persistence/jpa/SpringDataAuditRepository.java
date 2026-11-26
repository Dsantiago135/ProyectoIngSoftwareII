package co.edu.unicauca.saberpro.audit.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SpringDataAuditRepository extends JpaRepository<clsAuditEntity, UUID>,
        JpaSpecificationExecutor<clsAuditEntity> {
    boolean existsByEventId(UUID eventId);
    Optional<clsAuditEntity> findByEventId(UUID eventId);
}