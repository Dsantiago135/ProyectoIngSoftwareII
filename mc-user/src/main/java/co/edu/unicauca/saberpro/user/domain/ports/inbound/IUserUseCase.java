package co.edu.unicauca.saberpro.user.domain.ports.inbound;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.model.enmRole;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsToken;
import java.util.List;
import java.util.UUID;

public interface IUserUseCase {
    record rqtUser(String username, String password, String email, enmRole role) { }
    record rqtLogin(String username, String password) { }
    record rpsUser(UUID id, String username, String email, enmRole role, boolean active) { }
    rpsUser opRegister(rqtUser prmUser, UUID prmActorId);
    rpsToken opLogin(rqtLogin prmLogin);
    rpsUser opFindById(UUID prmId);
    List<rpsUser> opFindAll();
    rpsUser opUpdate(UUID prmId, rqtUser prmUser, UUID prmActorId);
    void opDeactivate(UUID prmId, UUID prmActorId);
    String opFindEmail(UUID prmId);
    rpsUser opToResponse(clsUser prmUser);
}