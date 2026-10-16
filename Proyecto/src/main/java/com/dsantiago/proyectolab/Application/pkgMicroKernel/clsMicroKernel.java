package com.dsantiago.proyectolab.Application.pkgMicroKernel;

import org.springframework.stereotype.Component;

import com.dsantiago.proyectolab.Application.pkgPlugins.IQuestionPlugin;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;
import com.dsantiago.proyectolab.Repository.IRepositoryQuestion;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
public class clsMicroKernel {

    private final clsPluginManager attPluginManager;
    private final IRepositoryQuestion attRepositoryQuestion;

    public clsMicroKernel(clsPluginManager prmPluginManager, IRepositoryQuestion prmRepositoryQuestion) {
        this.attPluginManager = prmPluginManager;
        this.attRepositoryQuestion = prmRepositoryQuestion;
    }

    /**
     * Generates and persists a question through its registered plugin.
     * @param prmRequest validated question request
     * @return persisted question
     */
    public clsQuestion opCreateQuestion(rqtQuestion prmRequest) {

        IQuestionPlugin varPlugin = attPluginManager.opGetPlugin(prmRequest.attType());
        
        clsQuestion varQuestion  = varPlugin.opGenerateQuestion(prmRequest);
        
        varQuestion = attRepositoryQuestion.save(varQuestion);
        log.info("Question created and saved successfully.");
        return varQuestion;
    }
}