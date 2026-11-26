package co.edu.unicauca.saberpro.user.domain.ports.outbound;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepositoryPort {
    clsUser opSave(clsUser prmUser);
    Optional<clsUser> opFindById(UUID prmId);
    Optional<clsUser> opFindByUsername(String prmUsername);
    List<clsUser> opFindAll();
    boolean opExistsByUsername(String prmUsername);
}