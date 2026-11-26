package co.edu.unicauca.saberpro.messaging.domain.ports.outbound;

import co.edu.unicauca.saberpro.messaging.domain.model.clsEmailMessage;

/** Puerto de salida para entregar un correo al proveedor configurado. */
public interface IEmailSenderPort {
    void opSend(clsEmailMessage prmMessage);
}