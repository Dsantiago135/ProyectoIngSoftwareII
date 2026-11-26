package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.user.domain.model.enmRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidad JPA*/
@Entity
@Table(name = "users")
public class clsUserEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String username;
    @Column(nullable = false, length = 100) private String passwordHash;
    @Column(nullable = false, length = 254) private String email;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private enmRole role;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private Instant createdAt;

    protected clsUserEntity() { }

    public clsUserEntity(UUID prmId, String prmUsername, String prmPasswordHash, String prmEmail,
                         enmRole prmRole, boolean prmActive, Instant prmCreatedAt) {
        id = prmId; username = prmUsername; passwordHash = prmPasswordHash; email = prmEmail;
        role = prmRole; active = prmActive; createdAt = prmCreatedAt;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public enmRole getRole() { return role; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}