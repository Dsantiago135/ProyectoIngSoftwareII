package co.edu.unicauca.saberpro.messaging.infrastructure.config;

import co.edu.unicauca.saberpro.messaging.application.service.clsNotificationService;
import co.edu.unicauca.saberpro.messaging.domain.ports.inbound.ISendNotificationUseCase;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IEmailSenderPort;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IRecipientLookupPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {
    @Bean
    ISendNotificationUseCase opNotificationUseCase(IEmailSenderPort prmEmailSender,
                                                     IRecipientLookupPort prmRecipientLookup) {
        return new clsNotificationService(prmEmailSender, prmRecipientLookup);
    }
}