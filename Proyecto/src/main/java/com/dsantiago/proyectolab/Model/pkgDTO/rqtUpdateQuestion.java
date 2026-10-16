package com.dsantiago.proyectolab.Model.pkgDTO;

import java.util.List;

import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;

public record rqtUpdateQuestion(int attId, String attContext, String attQuestion,
    List<String> attAnswers, String attCorrectAnswer, String attBibliography, String attTopic,
    String attSubTopic, EnumDificulty attDificulty) {
}