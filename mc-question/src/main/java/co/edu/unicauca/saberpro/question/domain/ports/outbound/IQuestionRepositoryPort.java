package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para guardar y consultar preguntas. */
public interface IQuestionRepositoryPort {
    clsQuestion opSave(clsQuestion prmQuestion);
    Optional<clsQuestion> opFindById(UUID prmId);
}