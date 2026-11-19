package co.edu.unicauca.saberpro.user.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Entidad de dominio del usuario, independiente de JPA y Spring. */
public final class clsUser {
    private final UUID attId;
    private final String attUsername;
    private String attPasswordHash;
    private String attEmail;
    private enmRole attRole;
    private boolean attActive;
    private final Instant attCreatedAt;

    private clsUser(UUID prmId, String prmUsername, String prmPasswordHash, String prmEmail,
                    enmRole prmRole, boolean prmActive, Instant prmCreatedAt) {
        attId = prmId;
        attUsername = prmUsername;
        attPasswordHash = prmPasswordHash;
        attEmail = prmEmail;
        attRole = prmRole;
        attActive = prmActive;
        attCreatedAt = prmCreatedAt;
    }

    /** Crea un usuario nuevo con credenciales ya cifradas. */
    public static clsUser opCreate(String prmUsername, String prmPasswordHash, String prmEmail, enmRole prmRole) {
        return new clsUser(UUID.randomUUID(), prmUsername, prmPasswordHash, prmEmail, prmRole, true, Instant.now());
    }

    /** Reconstruye un usuario desde el adaptador de persistencia. */
    public static clsUser opRebuild(UUID prmId, String prmUsername, String prmPasswordHash, String prmEmail,
                                    enmRole prmRole, boolean prmActive, Instant prmCreatedAt) {
        return new clsUser(prmId, prmUsername, prmPasswordHash, prmEmail, prmRole, prmActive, prmCreatedAt);
    }

    /** Actualiza datos públicos y reemplaza la clave solo si se entrega una nueva. */
    public void opUpdate(String prmEmail, enmRole prmRole, String prmPasswordHash) {
        attEmail = prmEmail;
        attRole = prmRole;
        if (prmPasswordHash != null) {
            attPasswordHash = prmPasswordHash;
        }
    }

    /** Desactiva la cuenta sin eliminar el registro. */
    public void opDeactivate() { attActive = false; }
    public UUID getId() { return attId; }
    public String getUsername() { return attUsername; }
    public String getPasswordHash() { return attPasswordHash; }
    public String getEmail() { return attEmail; }
    public enmRole getRole() { return attRole; }
    public boolean isActive() { return attActive; }
    public Instant getCreatedAt() { return attCreatedAt; }
}