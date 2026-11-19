package co.edu.unicauca.saberpro.user.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.user.domain.model.enmRole;
import co.edu.unicauca.saberpro.user.domain.ports.inbound.IUserUseCase;
import co.edu.unicauca.saberpro.user.domain.ports.inbound.IUserUseCase.rpsUser;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsToken;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador REST para login y administración de usuarios. */
@RestController
public class UserRestController {
    private final IUserUseCase attUserUseCase;
    public UserRestController(IUserUseCase prmUserUseCase) { attUserUseCase = prmUserUseCase; }

    /** Valida las credenciales y devuelve un JWT de acceso. */
    @PostMapping("/api/auth/login")
    public rpsToken opLogin(@RequestBody rqtLogin prmRequest) {
        return attUserUseCase.opLogin(new IUserUseCase.rqtLogin(prmRequest.username(), prmRequest.password()));
    }

    /** Registra un usuario y genera un evento de bienvenida. */
    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    public rpsUser opRegister(@RequestBody rqtUser prmRequest, @RequestHeader("X-User-Id") UUID prmActorId) {
        return attUserUseCase.opRegister(opToRequest(prmRequest), prmActorId);
    }

    /** Lista los usuarios registrados. */
    @GetMapping("/api/users")
    public List<rpsUser> opFindAll() { return attUserUseCase.opFindAll(); }

    /** Consulta un usuario por identificador. */
    @GetMapping("/api/users/{id}")
    public rpsUser opFindById(@PathVariable UUID id) { return attUserUseCase.opFindById(id); }

    /** Actualiza correo, rol y opcionalmente la contraseña. */
    @PutMapping("/api/users/{id}")
    public rpsUser opUpdate(@PathVariable UUID id, @RequestBody rqtUser prmRequest,
                            @RequestHeader("X-User-Id") UUID prmActorId) {
        return attUserUseCase.opUpdate(id, opToRequest(prmRequest), prmActorId);
    }

    /** Desactiva una cuenta sin borrar sus datos. */
    @PatchMapping("/api/users/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void opDeactivate(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID prmActorId) {
        attUserUseCase.opDeactivate(id, prmActorId);
    }

    /** Consulta un correo para notificaciones internas de MCMessaging. */
    @GetMapping("/internal/users/{id}/email")
    public rpsRecipientEmail opFindEmail(@PathVariable UUID id) {
        return new rpsRecipientEmail(attUserUseCase.opFindEmail(id));
    }

    private IUserUseCase.rqtUser opToRequest(rqtUser prmRequest) {
        return new IUserUseCase.rqtUser(prmRequest.username(), prmRequest.password(), prmRequest.email(), prmRequest.role());
    }

    public record rqtLogin(String username, String password) { }
    public record rqtUser(String username, String password, String email, enmRole role) { }
    public record rpsRecipientEmail(String email) { }
}