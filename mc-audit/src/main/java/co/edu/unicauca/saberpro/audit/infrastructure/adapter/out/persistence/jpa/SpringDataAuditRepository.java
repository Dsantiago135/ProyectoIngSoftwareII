package co.edu.unicauca.saberpro.audit.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Repositorio de consultas y alta para MCAudit. */
public interface SpringDataAuditRepository extends JpaRepository<clsAuditEntity, UUID>,
        JpaSpecificationExecutor<clsAuditEntity> {
    boolean existsByEventId(UUID eventId);
    Optional<clsAuditEntity> findByEventId(UUID eventId);
}