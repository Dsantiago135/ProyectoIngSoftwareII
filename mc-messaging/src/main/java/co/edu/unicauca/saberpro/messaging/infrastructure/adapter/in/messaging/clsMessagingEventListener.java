package co.edu.unicauca.saberpro.messaging.infrastructure.adapter.in.messaging;

import co.edu.unicauca.saberpro.messaging.domain.model.rqtEmailEvent;
import co.edu.unicauca.saberpro.messaging.domain.ports.inbound.ISendNotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Consume avisos JSON de RabbitMQ y delega su entrega al caso de uso. */
@Component
public class clsMessagingEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(clsMessagingEventListener.class);
    private final ISendNotificationUseCase attSendNotification;
    private final ObjectMapper attMapper;
    public clsMessagingEventListener(ISendNotificationUseCase prmSendNotification, ObjectMapper prmMapper) {
        attSendNotification = prmSendNotification; attMapper = prmMapper;
    }

    @RabbitListener(queues = "messaging.queue")
    public void opOnEvent(String prmPayload) {
        rqtEmailEvent event;
        try {
            event = attMapper.readValue(prmPayload, rqtEmailEvent.class);
        } catch (Exception exception) {
            LOGGER.error("El aviso no contiene JSON válido; se envía a DLQ", exception);
            throw new AmqpRejectAndDontRequeueException("Aviso inválido", exception);
        }
        attSendNotification.opSend(event);
    }
}