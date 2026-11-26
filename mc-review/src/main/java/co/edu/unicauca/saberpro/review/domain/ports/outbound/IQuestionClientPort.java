package co.edu.unicauca.saberpro.review.domain.ports.outbound;

import co.edu.unicauca.saberpro.review.domain.model.clsQuestionSummary;
import co.edu.unicauca.saberpro.review.domain.model.enmQuestionStatus;
import java.util.UUID;

/** Puerto de salida para consultar y cambiar el estado de preguntas en MCQuestion. */
public interface IQuestionClientPort {
    clsQuestionSummary opGetQuestion(UUID prmQuestionId);
    void opChangeStatus(UUID prmQuestionId, enmQuestionStatus prmStatus);
}