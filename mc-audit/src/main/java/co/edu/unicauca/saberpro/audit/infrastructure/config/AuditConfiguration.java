package co.edu.unicauca.saberpro.audit.infrastructure.config;

import co.edu.unicauca.saberpro.audit.application.service.clsAuditService;
import co.edu.unicauca.saberpro.audit.domain.ports.outbound.IAuditRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AuditConfiguration {
    @Bean
    clsAuditService opAuditService(IAuditRepositoryPort prmRepository, ObjectMapper prmMapper) {
        return new clsAuditService(prmRepository, prmMapper);
    }
}