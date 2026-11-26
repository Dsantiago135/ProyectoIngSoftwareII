package co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.IQuestionUseCase;
import co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web.dto.rqtQuestion;
import co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web.dto.rqtStatusChange;
import co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web.dto.rpsQuestion;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionRestController {
    private final IQuestionUseCase attQuestionUseCase;

    public QuestionRestController(IQuestionUseCase prmQuestionUseCase) {
        attQuestionUseCase = prmQuestionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public rpsQuestion opCreate(@RequestBody rqtQuestion prmRequest,
                                @RequestHeader("X-User-Id") UUID prmActorId) {
        return opToResponse(attQuestionUseCase.opCreate(opToDraft(prmRequest), prmActorId));
    }

    @GetMapping("/{id}")
    public rpsQuestion opFindById(@PathVariable UUID id) {
        return opToResponse(attQuestionUseCase.opFindById(id));
    }

    @PostMapping("/{id}/submit")
    public rpsQuestion opSubmit(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID prmActorId) {
        return opToResponse(attQuestionUseCase.opSubmit(id, prmActorId));
    }

    @PatchMapping("/{id}/status")
    public rpsQuestion opChangeStatus(@PathVariable UUID id, @RequestBody rqtStatusChange prmRequest) {
        return opToResponse(attQuestionUseCase.opChangeStatus(id, prmRequest.status()));
    }

    private clsQuestionDraft opToDraft(rqtQuestion prmRequest) {
        return new clsQuestionDraft(prmRequest.contextType(), prmRequest.context(), prmRequest.mediaUrl(),
                prmRequest.directQuestion(), prmRequest.distractors(), prmRequest.correctAnswer(),
                prmRequest.justification(), prmRequest.bibliography(), prmRequest.competence(),
                prmRequest.topic(), prmRequest.subtopic(), prmRequest.difficulty());
    }

    private rpsQuestion opToResponse(clsQuestion prmQuestion) {
        return new rpsQuestion(prmQuestion.getId(), prmQuestion.getAuthorId(), prmQuestion.getContextType(),
                prmQuestion.getContext(), prmQuestion.getMediaUrl(), prmQuestion.getDirectQuestion(),
                prmQuestion.getDistractors(), prmQuestion.getCorrectAnswer(), prmQuestion.getJustification(),
                prmQuestion.getBibliography(), prmQuestion.getCompetence(), prmQuestion.getTopic(),
                prmQuestion.getSubtopic(), prmQuestion.getDifficulty(), prmQuestion.getStatus(),
                prmQuestion.getCreatedAt(), prmQuestion.getUpdatedAt());
    }
}