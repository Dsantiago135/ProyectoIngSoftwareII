package co.edu.unicauca.saberpro.review.domain.ports.outbound;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import java.util.UUID;

/** Puerto de salida para los eventos de revisión y avisos al autor. */
public interface IEventPublisherPort {
    void opSendAudit(String prmEventType, UUID prmActorId, clsReview prmReview);
    void opSendEmail(String prmEventType, UUID prmActorId, UUID prmRecipientId, clsReview prmReview);
}