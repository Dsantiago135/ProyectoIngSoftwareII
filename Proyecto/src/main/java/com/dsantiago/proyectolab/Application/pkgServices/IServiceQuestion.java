package com.dsantiago.proyectolab.Application.pkgServices;

import java.util.List;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtAssignReviewer;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtChangeStatus;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUpdateQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;

public interface IServiceQuestion {

    /**
     * Validates and creates a question.
     * @param prmRequest complete question data
     * @return persisted question
     */
    clsQuestion opCreateQuestion(rqtQuestion prmRequest);

    /**
     * Updates a question that remains in draft state.
     * @param prmRequest replacement question data
     * @return updated question
     */
    clsQuestion opUpdateQuestion(rqtUpdateQuestion prmRequest);

    /**
     * Assigns the reviewers included in the request.
     * @param prmRequest question and reviewer usernames
     * @return updated question
     */
    clsQuestion opAssignReviewer(rqtAssignReviewer prmRequest);

    /**
     * Changes a question status when its transition is valid.
     * @param prmRequest question id and requested status
     * @return updated question
     */
    clsQuestion opChangeStatus(rqtChangeStatus prmRequest);

    /**
     * Retrieves questions authored by the supplied user.
     * @param prmUsername author username
     * @return authored questions
     */
    List<clsQuestion> opGetQuestionByAuthor(String prmUsername);
}