package co.edu.unicauca.saberpro.messaging.infrastructure.adapter.out.mail;

import co.edu.unicauca.saberpro.messaging.domain.exception.clsNotificationException;
import co.edu.unicauca.saberpro.messaging.domain.model.clsEmailMessage;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IEmailSenderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Adaptador SMTP que entrega mensajes de texto al proveedor configurado. */
@Component
public class clsEmailSenderAdapter implements IEmailSenderPort {
    private final JavaMailSender attMailSender;
    private final String attFrom;
    public clsEmailSenderAdapter(JavaMailSender prmMailSender,
                                 @Value("${messaging.sender}") String prmFrom) {
        attMailSender = prmMailSender;
        attFrom = prmFrom;
    }

    @Override
    public void opSend(clsEmailMessage prmMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(attFrom);
            message.setTo(prmMessage.to());
            message.setSubject(prmMessage.subject());
            message.setText(prmMessage.body());
            attMailSender.send(message);
        } catch (RuntimeException exception) {
            throw new clsNotificationException("No fue posible enviar el correo", exception);
        }
    }
}