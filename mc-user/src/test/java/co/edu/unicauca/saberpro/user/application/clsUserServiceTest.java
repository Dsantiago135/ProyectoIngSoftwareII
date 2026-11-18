package co.edu.unicauca.saberpro.user.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unicauca.saberpro.user.application.service.clsUserService;
import co.edu.unicauca.saberpro.user.domain.exception.clsUserException;
import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.model.enmRole;
import co.edu.unicauca.saberpro.user.domain.ports.inbound.IUserUseCase;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IPasswordHashPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IUserRepositoryPort;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class clsUserServiceTest {
    @Test
    void registersHashedUserAndPublishesWelcomeEvent() {
        Fixture fixture = new Fixture();
        IUserUseCase.rpsUser response = fixture.service.opRegister(
                new IUserUseCase.rqtUser("ana", "password1", "ana@example.test", enmRole.AUTHOR), UUID.randomUUID());

        assertEquals("ana", response.username());
        assertEquals(1, fixture.events.emails);
        assertEquals("hashed:password1", fixture.repository.users.values().iterator().next().getPasswordHash());
    }

    @Test
    void duplicateUsernameReturnsConflict() {
        Fixture fixture = new Fixture();
        fixture.service.opRegister(new IUserUseCase.rqtUser("ana", "password1", "ana@example.test", enmRole.AUTHOR), UUID.randomUUID());

        clsUserException exception = assertThrows(clsUserException.class, () -> fixture.service.opRegister(
                new IUserUseCase.rqtUser("ana", "password2", "other@example.test", enmRole.STUDENT), UUID.randomUUID()));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void successfulLoginReturnsTokenAndAuditsWithoutExposingPassword() {
        Fixture fixture = new Fixture();
        fixture.service.opRegister(new IUserUseCase.rqtUser("ana", "password1", "ana@example.test", enmRole.AUTHOR), UUID.randomUUID());

        ITokenPort.rpsToken token = fixture.service.opLogin(new IUserUseCase.rqtLogin("ana", "password1"));

        assertEquals("signed-token", token.token());
        assertEquals(1, fixture.events.audits);
    }

    @Test
    void deactivatedUserGetsSameUnauthorizedResponseAsBadPassword() {
        Fixture fixture = new Fixture();
        IUserUseCase.rpsUser created = fixture.service.opRegister(
                new IUserUseCase.rqtUser("ana", "password1", "ana@example.test", enmRole.AUTHOR), UUID.randomUUID());
        fixture.service.opDeactivate(created.id(), UUID.randomUUID());

        clsUserException exception = assertThrows(clsUserException.class,
                () -> fixture.service.opLogin(new IUserUseCase.rqtLogin("ana", "password1")));

        assertEquals(401, exception.getStatus());
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    private static final class Fixture {
        private final InMemoryUsers repository = new InMemoryUsers();
        private final EventCounter events = new EventCounter();
        private final clsUserService service = new clsUserService(repository,
                new IPasswordHashPort() {
                    @Override public String opHash(String prmPassword) { return "hashed:" + prmPassword; }
                    @Override public boolean opMatches(String prmPassword, String prmHash) { return prmHash.equals("hashed:" + prmPassword); }
                }, new ITokenPort() {
                    @Override public rpsToken opGenerate(clsUser prmUser) { return new rpsToken("signed-token", Instant.now().plusSeconds(86400)); }
                    @Override public rpsJwks opGetPublicKeys() { return new rpsJwks(List.of()); }
                }, events);
    }

    private static final class InMemoryUsers implements IUserRepositoryPort {
        private final Map<UUID, clsUser> users = new HashMap<>();
        @Override public clsUser opSave(clsUser prmUser) { users.put(prmUser.getId(), prmUser); return prmUser; }
        @Override public Optional<clsUser> opFindById(UUID prmId) { return Optional.ofNullable(users.get(prmId)); }
        @Override public Optional<clsUser> opFindByUsername(String prmUsername) {
            return users.values().stream().filter(user -> user.getUsername().equals(prmUsername)).findFirst();
        }
        @Override public List<clsUser> opFindAll() { return new ArrayList<>(users.values()); }
        @Override public boolean opExistsByUsername(String prmUsername) { return opFindByUsername(prmUsername).isPresent(); }
    }

    private static final class EventCounter implements IEventPublisherPort {
        private int audits;
        private int emails;
        @Override public void opSendAudit(String prmEventType, UUID prmActorId, UUID prmEntityId, Map<String, Object> prmDetail) { audits++; }
        @Override public void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, String prmEmail,
                                          String prmTemplate, Map<String, Object> prmVariables) { emails++; }
    }
}