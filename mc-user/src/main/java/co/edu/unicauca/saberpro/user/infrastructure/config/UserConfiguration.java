package co.edu.unicauca.saberpro.user.infrastructure.config;

import co.edu.unicauca.saberpro.user.application.service.clsUserService;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IPasswordHashPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.IUserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Raíz de composición de MCUser. */
@Configuration
public class UserConfiguration {
    @Bean
    clsUserService opUserService(IUserRepositoryPort prmRepository, IPasswordHashPort prmPasswordHash,
                                 ITokenPort prmToken, IEventPublisherPort prmEvents) {
        return new clsUserService(prmRepository, prmPasswordHash, prmToken, prmEvents);
    }
}