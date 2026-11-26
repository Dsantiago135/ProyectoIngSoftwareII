package co.edu.unicauca.saberpro.user.domain.model;

import java.time.Instant;
import java.util.UUID;

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

    public static clsUser opCreate(String prmUsername, String prmPasswordHash, String prmEmail, enmRole prmRole) {
        return new clsUser(UUID.randomUUID(), prmUsername, prmPasswordHash, prmEmail, prmRole, true, Instant.now());
    }

    public static clsUser opRebuild(UUID prmId, String prmUsername, String prmPasswordHash, String prmEmail,
                                    enmRole prmRole, boolean prmActive, Instant prmCreatedAt) {
        return new clsUser(prmId, prmUsername, prmPasswordHash, prmEmail, prmRole, prmActive, prmCreatedAt);
    }

    public void opUpdate(String prmEmail, enmRole prmRole, String prmPasswordHash) {
        attEmail = prmEmail;
        attRole = prmRole;
        if (prmPasswordHash != null) {
            attPasswordHash = prmPasswordHash;
        }
    }

    public void opDeactivate() { attActive = false; }
    public UUID getId() { return attId; }
    public String getUsername() { return attUsername; }
    public String getPasswordHash() { return attPasswordHash; }
    public String getEmail() { return attEmail; }
    public enmRole getRole() { return attRole; }
    public boolean isActive() { return attActive; }
    public Instant getCreatedAt() { return attCreatedAt; }
}