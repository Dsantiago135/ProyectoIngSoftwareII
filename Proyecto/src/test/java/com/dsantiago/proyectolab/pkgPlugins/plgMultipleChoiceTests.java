package com.dsantiago.proyectolab.pkgPlugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dsantiago.proyectolab.Application.pkgPlugins.plgMultipleChoice;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;
import com.dsantiago.proyectolab.Model.pkgModels.EnumStatus;

class plgMultipleChoiceTests {

    /** Verifies plugin type selection. */
    @Test
    void opSupports_AcceptsMultipleChoiceType() {
        assertTrue(new plgMultipleChoice().opSupports("multiple_choice"));
    }

    /** Verifies request fields are mapped to a draft question entity. */
    @Test
    void opGenerateQuestion_MapsUmlFieldsAndDraftStatus() {
        rqtQuestion request = new rqtQuestion("Context", "Question?",
            List.of("Distractor one", "Distractor two", "Distractor three", "Distractor four"),
            "Correct answer", "Bibliography", "Competence", "Topic", EnumDificulty.LOW,
            "MULTIPLE_CHOICE", "author");

        var question = new plgMultipleChoice().opGenerateQuestion(request);

        assertEquals("Question?", question.getAttQuestion());
        assertEquals(EnumStatus.DRAFT, question.getAttStatus());
        assertEquals("author", question.getAttAuthor().getAttUsername());
    }
}