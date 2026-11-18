package co.edu.unicauca.saberpro.user.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara el exchange de eventos usado por los productores. */
@Configuration
public class UserRabbitTopologyConfiguration {
    @Bean
    TopicExchange opApplicationEventsExchange() {
        return new TopicExchange("app.events", true, false);
    }
}