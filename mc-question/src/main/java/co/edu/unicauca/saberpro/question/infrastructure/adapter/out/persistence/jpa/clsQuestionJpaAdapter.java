package co.edu.unicauca.saberpro.question.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestionDraft;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.IQuestionRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class clsQuestionJpaAdapter implements IQuestionRepositoryPort {
    private final SpringDataQuestionRepository attRepository;

    public clsQuestionJpaAdapter(SpringDataQuestionRepository prmRepository) {
        attRepository = prmRepository;
    }

    @Override
    public clsQuestion opSave(clsQuestion prmQuestion) {
        return opToDomain(attRepository.save(opToEntity(prmQuestion)));
    }

    @Override
    public Optional<clsQuestion> opFindById(UUID prmId) {
        return attRepository.findById(prmId).map(this::opToDomain);
    }

    private clsQuestionEntity opToEntity(clsQuestion prmQuestion) {
        return new clsQuestionEntity(prmQuestion.getId(), prmQuestion.getAuthorId(), prmQuestion.getContextType(),
                prmQuestion.getContext(), prmQuestion.getMediaUrl(), prmQuestion.getDirectQuestion(),
                prmQuestion.getDistractors(), prmQuestion.getCorrectAnswer(), prmQuestion.getJustification(),
                prmQuestion.getBibliography(), prmQuestion.getCompetence(), prmQuestion.getTopic(),
                prmQuestion.getSubtopic(), prmQuestion.getDifficulty(), prmQuestion.getStatus(),
                prmQuestion.getCreatedAt(), prmQuestion.getUpdatedAt());
    }

    private clsQuestion opToDomain(clsQuestionEntity prmEntity) {
        clsQuestionDraft draft = new clsQuestionDraft(prmEntity.getContextType(), prmEntity.getContext(),
                prmEntity.getMediaUrl(), prmEntity.getDirectQuestion(), prmEntity.getDistractors(),
                prmEntity.getCorrectAnswer(), prmEntity.getJustification(), prmEntity.getBibliography(),
                prmEntity.getCompetence(), prmEntity.getTopic(), prmEntity.getSubtopic(), prmEntity.getDifficulty());
        return clsQuestion.opRebuild(prmEntity.getId(), prmEntity.getAuthorId(), draft, prmEntity.getStatus(),
                prmEntity.getCreatedAt(), prmEntity.getUpdatedAt());
    }
}