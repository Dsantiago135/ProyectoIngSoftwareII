package com.dsantiago.proyectolab.Model.pkgDTO;

import java.util.List;

import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;

public record rqtQuestion(
        String attContext,
        String attQuestion,
        List<String> attAnswers,
        String attCorrectAnswer,
        String attBibliography,
        String attTopic,
        String attSubTopic,
        EnumDificulty attDificulty,
        String attType,
        String attAuthor
) {

    /**
     * Converts an rqtQuestion into a Question entity.
     * @return Question entity created from this rqtQuestion.
     */
    public clsQuestion toQuestion() {
        clsQuestion varQuestion = new clsQuestion();

        varQuestion.setAttContext(attContext);
        varQuestion.setAttQuestion(attQuestion);

        varQuestion.setAttAnswer1(attAnswers != null && attAnswers.size() > 0 ? attAnswers.get(0) : null);
        varQuestion.setAttAnswer2(attAnswers != null && attAnswers.size() > 1 ? attAnswers.get(1) : null);
        varQuestion.setAttAnswer3(attAnswers != null && attAnswers.size() > 2 ? attAnswers.get(2) : null);
        varQuestion.setAttAnswer4(attAnswers != null && attAnswers.size() > 3 ? attAnswers.get(3) : null);

        varQuestion.setAttCorrectAnswer(attCorrectAnswer);
        varQuestion.setAttBibliography(attBibliography);
        varQuestion.setAttTopic(attTopic);
        varQuestion.setAttSubTopic(attSubTopic);
        varQuestion.setAttDificulty(attDificulty);
        varQuestion.setAttType(attType);

        varQuestion.setAttAuthor(null);
        varQuestion.setAttReviewer1(null);
        varQuestion.setAttReviewer2(null);
        return varQuestion;
    }
}