package co.edu.unicauca.saberpro.messaging.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara la cola de avisos durable y su cola de mensajes muertos. */
@Configuration
public class MessagingRabbitTopologyConfiguration {
    @Bean
    Declarables opMessagingTopology() {
        TopicExchange events = new TopicExchange("app.events", true, false);
        DirectExchange deadExchange = new DirectExchange("app.dead", true, false);
        Queue messagingQueue = QueueBuilder.durable("messaging.queue")
                .deadLetterExchange("app.dead").deadLetterRoutingKey("messaging.dead").build();
        Queue deadQueue = QueueBuilder.durable("messaging.dead").build();
        Binding eventsBinding = BindingBuilder.bind(messagingQueue).to(events).with("auditable.aviso.#");
        Binding deadBinding = BindingBuilder.bind(deadQueue).to(deadExchange).with("messaging.dead");
        return new Declarables(events, deadExchange, messagingQueue, deadQueue, eventsBinding, deadBinding);
    }
}