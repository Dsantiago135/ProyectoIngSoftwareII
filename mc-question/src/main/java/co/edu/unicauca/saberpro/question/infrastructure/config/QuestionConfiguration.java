package co.edu.unicauca.saberpro.question.infrastructure.config;

import co.edu.unicauca.saberpro.question.application.service.clsQuestionService;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.IQuestionUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IQuestionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionConfiguration {
    @Bean
    IQuestionUseCase opQuestionUseCase(IQuestionRepositoryPort prmRepository,
                                       IEventPublisherPort prmEventPublisher) {
        return new clsQuestionService(prmRepository, prmEventPublisher);
    }
}