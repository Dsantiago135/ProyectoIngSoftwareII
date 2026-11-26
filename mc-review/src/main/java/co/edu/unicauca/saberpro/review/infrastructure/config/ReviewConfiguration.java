package co.edu.unicauca.saberpro.review.infrastructure.config;

import co.edu.unicauca.saberpro.review.application.service.clsReviewService;
import co.edu.unicauca.saberpro.review.domain.ports.inbound.IReviewUseCase;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IEventPublisherPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IQuestionClientPort;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IReviewRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewConfiguration {
    @Bean
    IReviewUseCase opReviewUseCase(IReviewRepositoryPort prmRepository, IQuestionClientPort prmQuestionClient,
                                   IEventPublisherPort prmEvents) {
        return new clsReviewService(prmRepository, prmQuestionClient, prmEvents);
    }
}