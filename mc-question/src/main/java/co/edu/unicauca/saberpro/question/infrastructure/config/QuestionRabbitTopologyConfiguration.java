package co.edu.unicauca.saberpro.question.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionRabbitTopologyConfiguration {
    @Bean
    TopicExchange opApplicationEventsExchange() {
        return new TopicExchange("app.events", true, false);
    }
}