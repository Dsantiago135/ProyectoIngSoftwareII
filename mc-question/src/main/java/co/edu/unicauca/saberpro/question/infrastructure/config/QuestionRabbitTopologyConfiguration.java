package co.edu.unicauca.saberpro.question.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara el exchange de eventos usado por MCQuestion. */
@Configuration
public class QuestionRabbitTopologyConfiguration {
    @Bean
    TopicExchange opApplicationEventsExchange() {
        return new TopicExchange("app.events", true, false);
    }
}