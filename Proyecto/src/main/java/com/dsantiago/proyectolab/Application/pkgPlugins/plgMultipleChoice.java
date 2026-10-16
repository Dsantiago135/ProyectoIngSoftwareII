package com.dsantiago.proyectolab.Application.pkgPlugins;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;


public class plgMultipleChoice implements IQuestionPlugin {

    /**
     * Returns the stable registry name for this plugin.
     * @return plugin name
     */
    @Override
    public String opGetName() {
        return "MULTIPLE_CHOICE";
    }

    /**
     * Checks the requested type without case sensitivity.
     * @param prmType requested question type
     * @return true when the type is multiple choice
     */
    @Override
    public boolean opSupports(String prmType) {
        return "MULTIPLE_CHOICE".equalsIgnoreCase(prmType);
    }

    /**
     * Maps a validated request into the question entity.
     * @param prmRequest question creation request
     * @return question in draft state
     */
    @Override
    public clsQuestion opGenerateQuestion(rqtQuestion prmRequest) {
        return prmRequest.toQuestion();
    }
}