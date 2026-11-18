package co.edu.unicauca.saberpro.review.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara el exchange de eventos usado por MCReview. */
@Configuration
public class ReviewRabbitTopologyConfiguration {
    @Bean
    TopicExchange opApplicationEventsExchange() {
        return new TopicExchange("app.events", true, false);
    }
}