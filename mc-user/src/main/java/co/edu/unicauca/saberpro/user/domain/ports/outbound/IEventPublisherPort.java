package co.edu.unicauca.saberpro.user.domain.ports.outbound;

import java.util.Map;
import java.util.UUID;

/** Puerto de salida para auditoría y avisos publicados a RabbitMQ. */
public interface IEventPublisherPort {
    void opSendAudit(String prmEventType, UUID prmActorId, UUID prmEntityId, Map<String, Object> prmDetail);
    void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, String prmEmail,
                     String prmTemplate, Map<String, Object> prmVariables);
}