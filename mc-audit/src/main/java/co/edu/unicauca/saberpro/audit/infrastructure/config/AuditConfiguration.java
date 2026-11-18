package co.edu.unicauca.saberpro.audit.infrastructure.config;

import co.edu.unicauca.saberpro.audit.application.service.clsAuditService;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IQueryAuditUseCase;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IRecordAuditUseCase;
import co.edu.unicauca.saberpro.audit.domain.ports.outbound.IAuditRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

/** Raíz de composición de MCAudit. */
@Configuration
public class AuditConfiguration {
    @Bean
    clsAuditService opAuditService(IAuditRepositoryPort prmRepository, ObjectMapper prmMapper) {
        return new clsAuditService(prmRepository, prmMapper);
    }
}