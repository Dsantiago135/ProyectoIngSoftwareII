package co.edu.unicauca.saberpro.user.application.service;

import co.edu.unicauca.saberpro.user.domain.exception.clsUserException;
import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.ports.inbound.IPublicKeyUseCase;
import co.edu.unicauca.saberpro.user.domain.ports.inbound.IUserUseCase;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IPasswordHashPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsJwks;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsToken;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IUserRepositoryPort;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class clsUserService implements IUserUseCase, IPublicKeyUseCase {
    private final IUserRepositoryPort attRepository;
    private final IPasswordHashPort attPasswordHash;
    private final ITokenPort attToken;
    private final IEventPublisherPort attEvents;

    public clsUserService(IUserRepositoryPort prmRepository, IPasswordHashPort prmPasswordHash,
                          ITokenPort prmToken, IEventPublisherPort prmEvents) {
        attRepository = prmRepository;
        attPasswordHash = prmPasswordHash;
        attToken = prmToken;
        attEvents = prmEvents;
    }

    @Override
    public rpsUser opRegister(rqtUser prmRequest, UUID prmActorId) {
        opValidateUser(prmRequest, true);
        if (attRepository.opExistsByUsername(prmRequest.username().trim())) {
            throw new clsUserException(409, "El nombre de usuario ya está registrado");
        }
        clsUser user = clsUser.opCreate(prmRequest.username().trim(), attPasswordHash.opHash(prmRequest.password()),
                prmRequest.email().trim(), prmRequest.role());
        user = attRepository.opSave(user);
        attEvents.opSendEmail("user.registered", prmActorId, user.getId(), user.getEmail(),
                "user.registered", Map.of("username", user.getUsername()));
        return opToResponse(user);
    }

    @Override
    public rpsToken opLogin(rqtLogin prmLogin) {
        if (prmLogin == null || prmLogin.username() == null || prmLogin.password() == null) {
            throw new clsUserException(401, "Credenciales inválidas");
        }
        clsUser user = attRepository.opFindByUsername(prmLogin.username().trim()).orElse(null);
        if (user == null || !user.isActive() || !attPasswordHash.opMatches(prmLogin.password(), user.getPasswordHash())) {
            throw new clsUserException(401, "Credenciales inválidas");
        }
        rpsToken token = attToken.opGenerate(user);
        attEvents.opSendAudit("user.login", user.getId(), user.getId(), Map.of("username", user.getUsername()));
        return token;
    }

    @Override
    public rpsUser opFindById(UUID prmId) { return opToResponse(opFindUser(prmId)); }

    @Override
    public List<rpsUser> opFindAll() { return attRepository.opFindAll().stream().map(this::opToResponse).toList(); }

    @Override
    public rpsUser opUpdate(UUID prmId, rqtUser prmRequest, UUID prmActorId) {
        opValidateUser(prmRequest, false);
        clsUser user = opFindUser(prmId);
        if (prmRequest.password() != null && !prmRequest.password().isBlank()) {
            opValidatePassword(prmRequest.password());
        }
        user.opUpdate(prmRequest.email().trim(), prmRequest.role(),
                prmRequest.password() == null || prmRequest.password().isBlank()
                        ? null : attPasswordHash.opHash(prmRequest.password()));
        user = attRepository.opSave(user);
        attEvents.opSendAudit("user.updated", prmActorId, user.getId(), Map.of("role", user.getRole().name()));
        return opToResponse(user);
    }

    @Override
    public void opDeactivate(UUID prmId, UUID prmActorId) {
        clsUser user = opFindUser(prmId);
        user.opDeactivate();
        attRepository.opSave(user);
        attEvents.opSendAudit("user.deactivated", prmActorId, user.getId(), Map.of("active", false));
    }

    @Override
    public String opFindEmail(UUID prmId) { return opFindUser(prmId).getEmail(); }

    @Override
    public rpsUser opToResponse(clsUser prmUser) {
        return new rpsUser(prmUser.getId(), prmUser.getUsername(), prmUser.getEmail(),
                prmUser.getRole(), prmUser.isActive());
    }

    @Override
    public rpsJwks opGetPublicKeys() { return attToken.opGetPublicKeys(); }

    private clsUser opFindUser(UUID prmId) {
        return attRepository.opFindById(prmId)
                .orElseThrow(() -> new clsUserException(404, "No existe el usuario solicitado"));
    }

    private void opValidateUser(rqtUser prmRequest, boolean prmPasswordRequired) {
        if (prmRequest == null || prmRequest.username() == null || prmRequest.username().isBlank()
                || prmRequest.email() == null || prmRequest.email().isBlank() || prmRequest.role() == null) {
            throw new clsUserException("Usuario, correo y rol son obligatorios");
        }
        if (prmPasswordRequired) opValidatePassword(prmRequest.password());
    }

    private void opValidatePassword(String prmPassword) {
        if (prmPassword == null || prmPassword.length() < 8) {
            throw new clsUserException("La contraseña debe tener al menos ocho caracteres");
        }
    }
}