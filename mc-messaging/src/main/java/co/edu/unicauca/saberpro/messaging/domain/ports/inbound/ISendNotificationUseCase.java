package co.edu.unicauca.saberpro.messaging.domain.ports.inbound;

import co.edu.unicauca.saberpro.messaging.domain.model.rqtEmailEvent;

/** Puerto de entrada para procesar un evento que genera una notificación. */
public interface ISendNotificationUseCase {
    void opSend(rqtEmailEvent prmEvent);
}