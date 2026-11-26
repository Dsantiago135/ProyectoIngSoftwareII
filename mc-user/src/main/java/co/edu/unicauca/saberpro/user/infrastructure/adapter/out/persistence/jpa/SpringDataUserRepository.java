package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<clsUserEntity, UUID> {
    Optional<clsUserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}