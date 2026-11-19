package co.edu.unicauca.saberpro.audit.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara la cola de auditoría durable y su cola de mensajes muertos. */
@Configuration
public class AuditRabbitTopologyConfiguration {
    @Bean
    Declarables opAuditTopology() {
        TopicExchange events = new TopicExchange("app.events", true, false);
        DirectExchange deadExchange = new DirectExchange("app.dead", true, false);
        Queue auditQueue = QueueBuilder.durable("audit.queue")
                .deadLetterExchange("app.dead").deadLetterRoutingKey("audit.dead").build();
        Queue deadQueue = QueueBuilder.durable("audit.dead").build();
        Binding eventsBinding = BindingBuilder.bind(auditQueue).to(events).with("auditable.#");
        Binding deadBinding = BindingBuilder.bind(deadQueue).to(deadExchange).with("audit.dead");
        return new Declarables(events, deadExchange, auditQueue, deadQueue, eventsBinding, deadBinding);
    }
}