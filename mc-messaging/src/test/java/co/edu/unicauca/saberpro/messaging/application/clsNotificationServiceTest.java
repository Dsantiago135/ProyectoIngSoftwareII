package co.edu.unicauca.saberpro.messaging.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unicauca.saberpro.messaging.application.service.clsNotificationService;
import co.edu.unicauca.saberpro.messaging.domain.exception.clsNotificationException;
import co.edu.unicauca.saberpro.messaging.domain.model.clsEmailMessage;
import co.edu.unicauca.saberpro.messaging.domain.model.rqtEmailEvent;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IEmailSenderPort;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IRecipientLookupPort;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class clsNotificationServiceTest {
    @Test
    void buildsWelcomeEmailAndUsesEmailFromEvent() {
        AtomicReference<clsEmailMessage> sent = new AtomicReference<>();
        IEmailSenderPort sender = sent::set;
        IRecipientLookupPort lookup = id -> { throw new AssertionError("No se debe consultar MCUser"); };
        clsNotificationService service = new clsNotificationService(sender, lookup);
        rqtEmailEvent event = new rqtEmailEvent(UUID.randomUUID(), "user.registered", Instant.now(),
                "MCUser", UUID.randomUUID(), new rqtEmailEvent.rqtRecipient(UUID.randomUUID(), "user@example.test"),
                "user.registered", Map.of("username", "ana"));

        service.opSend(event);

        assertEquals("user@example.test", sent.get().to());
        assertEquals("Bienvenido a Saber Pro", sent.get().subject());
        assertEquals("Hola ana, tu cuenta de Saber Pro ya está activa.", sent.get().body());
    }

    @Test
    void resolvesEmailByUserIdWhenEventDoesNotIncludeIt() {
        AtomicReference<clsEmailMessage> sent = new AtomicReference<>();
        UUID userId = UUID.randomUUID();
        clsNotificationService service = new clsNotificationService(sent::set, id -> "resolved@example.test");
        rqtEmailEvent event = new rqtEmailEvent(UUID.randomUUID(), "question.approved", Instant.now(),
                "MCReview", null, new rqtEmailEvent.rqtRecipient(userId, null), "question.approved", Map.of());

        service.opSend(event);

        assertEquals("resolved@example.test", sent.get().to());
    }

    @Test
    void rejectsUnknownTemplate() {
        clsNotificationService service = new clsNotificationService(message -> { }, id -> "x@example.test");
        rqtEmailEvent event = new rqtEmailEvent(UUID.randomUUID(), "unknown.event", Instant.now(),
                "test", null, new rqtEmailEvent.rqtRecipient(UUID.randomUUID(), "x@example.test"), "unknown", Map.of());

        assertThrows(clsNotificationException.class, () -> service.opSend(event));
    }
}