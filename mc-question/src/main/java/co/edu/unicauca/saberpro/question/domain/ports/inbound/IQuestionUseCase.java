package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import java.util.UUID;

public interface IQuestionUseCase {
    clsQuestion opCreate(clsQuestionDraft prmDraft, UUID prmActorId);
    clsQuestion opFindById(UUID prmId);
    clsQuestion opSubmit(UUID prmId, UUID prmActorId);
    clsQuestion opChangeStatus(UUID prmId, enmQuestionStatus prmStatus);
}