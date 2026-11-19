package co.edu.unicauca.saberpro.user.infrastructure.config;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.model.enmRole;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IPasswordHashPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IUserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Crea el primer ADMIN solo cuando se entrega explícitamente por entorno. */
@Configuration
public class UserBootstrapConfiguration {
    @Bean
    CommandLineRunner opSeedAdministrator(IUserRepositoryPort prmRepository, IPasswordHashPort prmPasswordHash,
            @Value("${bootstrap.admin.username:}") String prmUsername,
            @Value("${bootstrap.admin.password:}") String prmPassword,
            @Value("${bootstrap.admin.email:}") String prmEmail) {
        return args -> {
            if (prmUsername.isBlank() && prmPassword.isBlank() && prmEmail.isBlank()) return;
            if (prmUsername.isBlank() || prmPassword.length() < 8 || prmEmail.isBlank()) {
                throw new IllegalStateException("La semilla ADMIN requiere username, email y contraseña de al menos ocho caracteres");
            }
            if (!prmRepository.opExistsByUsername(prmUsername.trim())) {
                prmRepository.opSave(clsUser.opCreate(prmUsername.trim(), prmPasswordHash.opHash(prmPassword),
                        prmEmail.trim(), enmRole.ADMIN));
            }
        };
    }
}