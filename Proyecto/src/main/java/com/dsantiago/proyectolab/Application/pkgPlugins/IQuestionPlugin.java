package com.dsantiago.proyectolab.Application.pkgPlugins;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;

public interface IQuestionPlugin {

    /**
     * Returns the plugin's registration name.
     * @return plugin name
     */
    String opGetName();

    /**
     * Checks whether this plugin can generate the requested question type.
     * @param prmType requested question type
     * @return true when this plugin supports the type
     */
    boolean opSupports(String prmType);

    /**
     * Generates a question entity from validated request data.
     * @param prmRequest question creation request
     * @return generated question
     */
    clsQuestion opGenerateQuestion(rqtQuestion prmRequest);
}