package com.dsantiago.proyectolab.Application.pkgServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsantiago.proyectolab.Application.pkgMicroKernel.clsMicroKernel;
import com.dsantiago.proyectolab.Application.pkgUtil.clsValidatorQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtAssignReviewer;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtChangeStatus;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUpdateQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.EnumStatus;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryQuestion;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service  
public class clsServicesQuestion implements IServiceQuestion {

    private final clsMicroKernel attMicrokernel;
    private final IRepositoryQuestion attRepositoryQuestion;
    private final IRepositoryUser attRepositoryUser;

    public clsServicesQuestion(clsMicroKernel prmMicrokernel, IRepositoryQuestion prmRepositoryQuestion,
        IRepositoryUser prmRepositoryUser) {
        this.attMicrokernel = prmMicrokernel;
        this.attRepositoryQuestion = prmRepositoryQuestion;
        this.attRepositoryUser = prmRepositoryUser;
    }

    /** {@inheritDoc} */
    @Override
    public clsQuestion opCreateQuestion(rqtQuestion prmRequest) {
        clsValidatorQuestion.opValidateContext(prmRequest);
        clsValidatorQuestion.opValidateAnswer(prmRequest);
        log.info("Received question creation request from author: {}", prmRequest.attAuthor());
        
        clsQuestion varQuestion = attMicrokernel.opCreateQuestion(prmRequest);
        varQuestion.setAttAuthor(attRepositoryUser.findById(prmRequest.attAuthor()).orElseThrow());

        return varQuestion;
    }

    /** {@inheritDoc} */
    @Override
    public clsQuestion opUpdateQuestion(rqtUpdateQuestion prmRequest) {
        
        clsQuestion varQuestion = attRepositoryQuestion.findById(prmRequest.attId())
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + prmRequest.attId()));
        
        if (varQuestion.getAttStatus() != EnumStatus.DRAFT) {
            throw new IllegalStateException("Only draft questions can be updated");
        }

        clsValidatorQuestion.opValidateContext(prmRequest);
        clsValidatorQuestion.opValidateAnswer(prmRequest);
        varQuestion.setAttContext(prmRequest.attContext());
        varQuestion.setAttQuestion(prmRequest.attQuestion());
        varQuestion.setAttAnswer1(prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 0 ? prmRequest.attAnswers().get(0) : null);
        varQuestion.setAttAnswer2(prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 1 ? prmRequest.attAnswers().get(1) : null);
        varQuestion.setAttAnswer3(prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 2 ? prmRequest.attAnswers().get(2) : null);
        varQuestion.setAttAnswer4(prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 3 ? prmRequest.attAnswers().get(3) : null);
        varQuestion.setAttCorrectAnswer(prmRequest.attCorrectAnswer());
        varQuestion.setAttBibliography(prmRequest.attBibliography());
        varQuestion.setAttTopic(prmRequest.attTopic());
        varQuestion.setAttSubTopic(prmRequest.attSubTopic());
        varQuestion.setAttDificulty(prmRequest.attDificulty());
        log.info("Question updated: {}", prmRequest.attId());
        return attRepositoryQuestion.save(varQuestion);
    }

    /** {@inheritDoc} */
    @Override
    public clsQuestion opAssignReviewer(rqtAssignReviewer prmRequest) {
        clsQuestion varQuestion = attRepositoryQuestion.findById(prmRequest.attIdQuestion())
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + prmRequest.attIdQuestion()));
        
        if (varQuestion.getAttStatus() != EnumStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Reviewers can only be assigned to pending questions");
        }
    
        if (prmRequest.attReviewer1() == null || prmRequest.attReviewer1().isBlank()) {
            throw new IllegalArgumentException("At least one reviewer is required");
        }
    
        varQuestion.setAttReviewer1(attRepositoryUser.findById(prmRequest.attReviewer1()).orElseThrow());
        varQuestion.setAttReviewer2(prmRequest.attReviewer2() == null || prmRequest.attReviewer2().isBlank()
            ? null : attRepositoryUser.findById(prmRequest.attReviewer2()).orElseThrow());
        varQuestion.setAttStatus(EnumStatus.UNDER_REVIEW);
        log.info("Reviewers assigned to question: {}", prmRequest.attIdQuestion());
        return attRepositoryQuestion.save(varQuestion);
    }

    /** {@inheritDoc} */
    @Override
    public clsQuestion opChangeStatus(rqtChangeStatus prmRequest) {
        clsQuestion varQuestion = attRepositoryQuestion.findById(prmRequest.attIdQuestion())
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + prmRequest.attIdQuestion()));
        
        boolean varValidTransition = switch (varQuestion.getAttStatus()) {
            case DRAFT -> prmRequest.attStatus() == EnumStatus.PENDING_REVIEW;
            case PENDING_REVIEW -> prmRequest.attStatus() == EnumStatus.UNDER_REVIEW
                || prmRequest.attStatus() == EnumStatus.DRAFT;
            case UNDER_REVIEW -> prmRequest.attStatus() == EnumStatus.APPROVED
                || prmRequest.attStatus() == EnumStatus.REJECTED;
            case REJECTED -> prmRequest.attStatus() == EnumStatus.DRAFT
                || prmRequest.attStatus() == EnumStatus.FILED;
            case APPROVED -> prmRequest.attStatus() == EnumStatus.PUBLISHED
                || prmRequest.attStatus() == EnumStatus.FILED;
            case PUBLISHED -> prmRequest.attStatus() == EnumStatus.FILED;
            case FILED -> false;
        };

        if (!varValidTransition) {
            throw new IllegalStateException("Invalid question status transition");
        }

        varQuestion.setAttStatus(prmRequest.attStatus());
        log.info("Question status changed: {} -> {}", prmRequest.attIdQuestion(), prmRequest.attStatus());
        return attRepositoryQuestion.save(varQuestion);
    }

    /** {@inheritDoc} */
    @Override
    public List<clsQuestion> opGetQuestionByAuthor(String prmAuthor) {
        return attRepositoryQuestion.opFindByAuthor(new clsUser(prmAuthor, null, null, null, null));
    }

}