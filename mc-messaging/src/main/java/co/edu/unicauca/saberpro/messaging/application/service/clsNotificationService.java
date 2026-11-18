package co.edu.unicauca.saberpro.messaging.application.service;

import co.edu.unicauca.saberpro.messaging.domain.exception.clsNotificationException;
import co.edu.unicauca.saberpro.messaging.domain.model.clsEmailMessage;
import co.edu.unicauca.saberpro.messaging.domain.model.rqtEmailEvent;
import co.edu.unicauca.saberpro.messaging.domain.ports.inbound.ISendNotificationUseCase;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IEmailSenderPort;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IRecipientLookupPort;
import java.util.Map;

/** Convierte eventos de aviso en correos en español y delega su entrega. */
public class clsNotificationService implements ISendNotificationUseCase {
    private final IEmailSenderPort attEmailSender;
    private final IRecipientLookupPort attRecipientLookup;

    public clsNotificationService(IEmailSenderPort prmEmailSender, IRecipientLookupPort prmRecipientLookup) {
        attEmailSender = prmEmailSender;
        attRecipientLookup = prmRecipientLookup;
    }

    @Override
    public void opSend(rqtEmailEvent prmEvent) {
        if (prmEvent == null || prmEvent.eventId() == null || prmEvent.eventType() == null
                || prmEvent.recipient() == null) {
            throw new clsNotificationException("El evento de notificación está incompleto");
        }
        String email = prmEvent.recipient().email();
        if (email == null || email.isBlank()) {
            if (prmEvent.recipient().userId() == null) {
                throw new clsNotificationException("El evento no identifica al destinatario");
            }
            email = attRecipientLookup.opFindEmail(prmEvent.recipient().userId());
        }
        if (email == null || email.isBlank()) throw new clsNotificationException("No se encontró el correo del destinatario");
        clsEmailMessage message = opBuildMessage(prmEvent, email);
        attEmailSender.opSend(message);
    }

    private clsEmailMessage opBuildMessage(rqtEmailEvent prmEvent, String prmEmail) {
        Map<String, Object> variables = prmEvent.variables() == null ? Map.of() : prmEvent.variables();
        String username = opVariable(variables, "username", "usuario");
        return switch (prmEvent.eventType()) {
            case "user.registered" -> new clsEmailMessage(prmEmail, "Bienvenido a Saber Pro",
                    "Hola " + username + ", tu cuenta de Saber Pro ya está activa.");
            case "question.approved" -> new clsEmailMessage(prmEmail, "Tu pregunta fue aprobada",
                    "Hola " + username + ", tu pregunta fue aprobada por el equipo revisor.");
            case "question.rejected" -> new clsEmailMessage(prmEmail, "Tu pregunta requiere ajustes",
                    "Hola " + username + ", tu pregunta fue rechazada. Observación: "
                            + opVariable(variables, "justification", "Consulta la revisión en Saber Pro"));
            case "question.submitted" -> new clsEmailMessage(prmEmail, "Hay una pregunta pendiente de revisión",
                    "Se envió una pregunta al proceso de revisión.");
            default -> throw new clsNotificationException("No existe una plantilla para el evento " + prmEvent.eventType());
        };
    }

    private String opVariable(Map<String, Object> prmVariables, String prmName, String prmDefault) {
        Object value = prmVariables.get(prmName);
        return value == null || value.toString().isBlank() ? prmDefault : value.toString();
    }
}