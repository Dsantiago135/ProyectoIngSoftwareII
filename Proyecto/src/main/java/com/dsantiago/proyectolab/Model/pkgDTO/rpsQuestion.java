package com.dsantiago.proyectolab.Model.pkgDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;
import com.dsantiago.proyectolab.Model.pkgModels.EnumStatus;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;

public record rpsQuestion(
        int attId,
        String attContext,
        String attQuestion,
        List<String> attAnswers,
        String attCorrectAnswer,
        String attBibliography,
        String attTopic,
        String attSubTopic,
        EnumDificulty attDificulty,
        EnumStatus attStatus,
        String attAuthor,
        String attReviewer1,
        String attReviewer2,
        String attType) {
    
    /**
     * Converts a Question entity into an rpsQuestion.
     * @param prmQuestion Question to convert.
     */
    public rpsQuestion(clsQuestion prmQuestion) {
        this(   prmQuestion.getAttId(),
                prmQuestion.getAttContext(),
                prmQuestion.getAttQuestion(),
                Stream.of(prmQuestion.getAttAnswer1(),prmQuestion.getAttAnswer2(),
                          prmQuestion.getAttAnswer3(),prmQuestion.getAttAnswer4())
                    .filter(Objects::nonNull).toList(),
                prmQuestion.getAttCorrectAnswer(),
                prmQuestion.getAttBibliography(),
                prmQuestion.getAttTopic(),
                prmQuestion.getAttSubTopic(),
                prmQuestion.getAttDificulty(),
                prmQuestion.getAttStatus(),
                prmQuestion.getAttAuthor().getAttUsername(),
                prmQuestion.getAttReviewer1() != null ? prmQuestion.getAttReviewer1().getAttUsername() : null,
                prmQuestion.getAttReviewer2() != null ? prmQuestion.getAttReviewer2().getAttUsername() : null,
                prmQuestion.getAttType()
        );
    }

    /**
     * Converts an rpsQuestion into a Question entity.
     * @return Question entity created from this rpsQuestion.
     */
    public clsQuestion toQuestion() {
        clsQuestion varQuestion = new clsQuestion();

        varQuestion.setAttId(attId);
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
        varQuestion.setAttStatus(attStatus);
        varQuestion.setAttType(attType);

        varQuestion.setAttAuthor(null);
        varQuestion.setAttReviewer1(null);
        varQuestion.setAttReviewer2(null);
        return varQuestion;
    }
}