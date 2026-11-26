package co.edu.unicauca.saberpro.messaging.infrastructure.adapter.out.rest;

import co.edu.unicauca.saberpro.messaging.domain.exception.clsNotificationException;
import co.edu.unicauca.saberpro.messaging.domain.ports.outbound.IRecipientLookupPort;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Adaptador interno que consulta el email del usuario en MCUser. */
@Component
public class clsUserRestAdapter implements IRecipientLookupPort {
    private final RestClient attRestClient;
    public clsUserRestAdapter(RestClient.Builder prmBuilder,
                              @Value("${services.mc-user}") String prmBaseUrl) {
        attRestClient = prmBuilder.baseUrl(prmBaseUrl).build();
    }

    @Override
    public String opFindEmail(UUID prmUserId) {
        try {
            rpsRecipientEmail response = attRestClient.get()
                    .uri("/internal/users/{id}/email", prmUserId).retrieve().body(rpsRecipientEmail.class);
            return response == null ? null : response.email();
        } catch (RuntimeException exception) {
            throw new clsNotificationException("No fue posible consultar el email en MCUser", exception);
        }
    }

    private record rpsRecipientEmail(String email) { }
}