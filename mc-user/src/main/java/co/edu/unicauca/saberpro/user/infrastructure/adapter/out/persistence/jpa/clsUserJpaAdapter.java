package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IUserRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/** Adaptador JPA que implementa el puerto de usuarios. Usa de la "conexión" a la base de datos*/
@Repository
public class clsUserJpaAdapter implements IUserRepositoryPort {
    private final SpringDataUserRepository attRepository;
    public clsUserJpaAdapter(SpringDataUserRepository prmRepository) { attRepository = prmRepository; }

    @Override
    public clsUser opSave(clsUser prmUser) {
        return opToDomain(attRepository.save(new clsUserEntity(prmUser.getId(), prmUser.getUsername(),
                prmUser.getPasswordHash(), prmUser.getEmail(), prmUser.getRole(), prmUser.isActive(), prmUser.getCreatedAt())));
    }
    @Override public Optional<clsUser> opFindById(UUID prmId) { return attRepository.findById(prmId).map(this::opToDomain); }
    @Override public Optional<clsUser> opFindByUsername(String prmUsername) { return attRepository.findByUsername(prmUsername).map(this::opToDomain); }
    @Override public List<clsUser> opFindAll() { return attRepository.findAll().stream().map(this::opToDomain).toList(); }
    @Override public boolean opExistsByUsername(String prmUsername) { return attRepository.existsByUsername(prmUsername); }

    private clsUser opToDomain(clsUserEntity prmEntity) {
        return clsUser.opRebuild(prmEntity.getId(), prmEntity.getUsername(), prmEntity.getPasswordHash(),
                prmEntity.getEmail(), prmEntity.getRole(), prmEntity.isActive(), prmEntity.getCreatedAt());
    }
}