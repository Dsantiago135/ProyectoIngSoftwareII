package co.edu.unicauca.saberpro.review.infrastructure.adapter.out.rest;

import co.edu.unicauca.saberpro.review.domain.model.clsQuestionSummary;
import co.edu.unicauca.saberpro.review.domain.model.enmQuestionStatus;
import co.edu.unicauca.saberpro.review.domain.ports.outbound.IQuestionClientPort;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;

/** Adaptador HTTP síncrono hacia los endpoints internos de MCQuestion. */
@Component
public class clsQuestionRestAdapter implements IQuestionClientPort {
    private final RestClient attRestClient;
    public clsQuestionRestAdapter(RestClient.Builder prmBuilder,
                                  @Value("${services.mc-question}") String prmBaseUrl) {
        attRestClient = prmBuilder.baseUrl(prmBaseUrl).build();
    }

    @Override
    public clsQuestionSummary opGetQuestion(UUID prmQuestionId) {
        try {
            return attRestClient.get().uri("/api/questions/{id}", prmQuestionId)
                    .retrieve().body(clsQuestionSummary.class);
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value() == 404 ? 404 : 502;
            throw new co.edu.unicauca.saberpro.review.domain.exception.clsReviewException(status,
                    status == 404 ? "No existe la pregunta solicitada" : "MCQuestion no pudo consultar la pregunta");
        }
    }

    @Override
    public void opChangeStatus(UUID prmQuestionId, enmQuestionStatus prmStatus) {
        try {
            attRestClient.patch().uri("/api/questions/{id}/status", prmQuestionId)
                    .body(new rqtStatusChange(prmStatus)).retrieve().toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new co.edu.unicauca.saberpro.review.domain.exception.clsReviewException(502,
                    "MCQuestion rechazó el cambio de estado");
        }
    }

    private record rqtStatusChange(enmQuestionStatus status) { }
}