package com.dsantiago.proyectolab.Application.pkgUtil;

import java.util.ArrayList;
import java.util.List;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUpdateQuestion;

public final class clsValidatorQuestion {

    private clsValidatorQuestion() {}

    /**
     * Validates context, direct question, and required metadata.
     * @param prmRequest question request to validate
     * @throws IllegalArgumentException when one or more fields are invalid
     */
    public static void opValidateContext(rqtQuestion prmRequest) {
        if (prmRequest == null) {
            throw new IllegalArgumentException("Question request is required");
        }

        List<String> varErrors = new ArrayList<>();

        String varContext = prmRequest.attContext();
        if (varContext == null || varContext.isBlank() || varContext.length() < 50 || varContext.length() > 750)
            varErrors.add("Context length must be between 50 and 750 characters");
        

        String varQuestion = prmRequest.attQuestion();
        if (varQuestion == null || varQuestion.isBlank() || varQuestion.length() > 500) 
            varErrors.add("Question is required and must not exceed 500 characters");
        

        String varBibliography = prmRequest.attBibliography();
        if (varBibliography != null && (varBibliography.isBlank() || varBibliography.length() > 500)) 
            varErrors.add("Bibliography must not be blank or exceed 500 characters");
        

        String varTopic = prmRequest.attTopic();
        if (varTopic != null && varTopic.isBlank()) 
            varErrors.add("Topic must not be blank");

        String varSubTopic = prmRequest.attSubTopic();
        if (varSubTopic != null && varSubTopic.isBlank()) 
            varErrors.add("Subtopic must not be blank");

        String varType = prmRequest.attType();
        if (varType == null || varType.isBlank() || varType.length() > 50)
            varErrors.add("Question type is required and must not exceed 50 characters");

        if (prmRequest.attDificulty() == null)
            varErrors.add("Difficulty is required");

        if (prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 4) 
            varErrors.add("A maximum of four distractors is allowed");

        if (!varErrors.isEmpty())
            throw new IllegalArgumentException(String.join("; ", varErrors));
    }

    /**
     * Validates the correct answer and exactly four distinct distractors.
     * @param prmRequest question request to validate
     * @throws IllegalArgumentException when one or more answers are invalid
     */
    public static void opValidateAnswer(rqtQuestion prmRequest) {
        if (prmRequest == null) throw new IllegalArgumentException("Question request is required");

        List<String> varErrors = new ArrayList<>();

        String varCorrectAnswer = prmRequest.attCorrectAnswer();
        if (varCorrectAnswer == null || varCorrectAnswer.isBlank() || varCorrectAnswer.length() < 10 || varCorrectAnswer.length() > 100)
            varErrors.add("Correct answer length must be between 10 and 100 characters");
        
        List<String> varAnswers = prmRequest.attAnswers();

        if (varAnswers == null || varAnswers.size() != 4) {
            varErrors.add("Exactly four distractors are required");
        } else {
            for (String varAnswer : varAnswers) {
                if (varAnswer == null || varAnswer.isBlank()
                        || varAnswer.length() < 10 || varAnswer.length() > 100) {
                    varErrors.add("Each distractor must contain between 10 and 100 characters");
                }
            }
        }

        List<String> varAllAnswers = new ArrayList<>();

        if (varCorrectAnswer != null) varAllAnswers.add(varCorrectAnswer);

        if (varAnswers != null) varAllAnswers.addAll(varAnswers);

        long varDistinctCount = varAllAnswers.stream()
                .filter(varAnswer -> varAnswer != null)
                .map(varAnswer -> varAnswer.trim().toLowerCase())
                .distinct()
                .count();

        if (varDistinctCount != varAllAnswers.size()) varErrors.add("Answers must be unique");

        boolean varHasProhibitedAlternative = varAllAnswers.stream()
                .filter(varAnswer -> varAnswer != null)
                .map(varAnswer -> varAnswer.toLowerCase())
                .anyMatch(varAnswer ->
                        varAnswer.contains("todas las anteriores") || varAnswer.contains("ninguna de las anteriores"));

        if (varHasProhibitedAlternative) varErrors.add("Answers must not contain prohibited alternatives");

        if (!varErrors.isEmpty()) throw new IllegalArgumentException(String.join("; ", varErrors));
    }

    /**
     * Validates the context, question text, optional metadata, difficulty,
     * and maximum number of answers in a question update request.
     *
     * @param prmRequest question update request to validate
     * @throws IllegalArgumentException if the request is null or one or more
     *         fields do not satisfy the validation rules
     */
    public static void opValidateContext(rqtUpdateQuestion prmRequest) {
        if (prmRequest == null) {
            throw new IllegalArgumentException("Question request is required");
        }

        List<String> varErrors = new ArrayList<>();

        String varContext = prmRequest.attContext();
        if (varContext == null || varContext.isBlank() || varContext.length() < 50 || varContext.length() > 750)
            varErrors.add("Context length must be between 50 and 750 characters");
        

        String varQuestion = prmRequest.attQuestion();
        if (varQuestion == null || varQuestion.isBlank() || varQuestion.length() > 500) 
            varErrors.add("Question is required and must not exceed 500 characters");
        

        String varBibliography = prmRequest.attBibliography();
        if (varBibliography != null && (varBibliography.isBlank() || varBibliography.length() > 500)) 
            varErrors.add("Bibliography must not be blank or exceed 500 characters");
        

        String varTopic = prmRequest.attTopic();
        if (varTopic != null && varTopic.isBlank()) 
            varErrors.add("Topic must not be blank");

        String varSubTopic = prmRequest.attSubTopic();
        if (varSubTopic != null && varSubTopic.isBlank()) 
            varErrors.add("Subtopic must not be blank");

        if (prmRequest.attDificulty() == null)
            varErrors.add("Difficulty is required");

        if (prmRequest.attAnswers() != null && prmRequest.attAnswers().size() > 4) 
            varErrors.add("A maximum of four distractors is allowed");

        if (!varErrors.isEmpty())
            throw new IllegalArgumentException(String.join("; ", varErrors));
    }

    /**
     * Validates the correct answer and the four distractors provided
     * in a question update request.
     *
     * @param prmRequest question update request containing the answers to validate
     * @throws IllegalArgumentException if the request is null or one or more
     *         answers violate the validation rules
     */
    public static void opValidateAnswer(rqtUpdateQuestion prmRequest) {
        if (prmRequest == null) throw new IllegalArgumentException("Question request is required");

        List<String> varErrors = new ArrayList<>();

        String varCorrectAnswer = prmRequest.attCorrectAnswer();
        if (varCorrectAnswer == null || varCorrectAnswer.isBlank() || varCorrectAnswer.length() < 10 || varCorrectAnswer.length() > 100)
            varErrors.add("Correct answer length must be between 10 and 100 characters");
        
        List<String> varAnswers = prmRequest.attAnswers();

        if (varAnswers == null || varAnswers.size() != 4) {
            varErrors.add("Exactly four distractors are required");
        } else {
            for (String varAnswer : varAnswers) {
                if (varAnswer == null || varAnswer.isBlank()
                        || varAnswer.length() < 10 || varAnswer.length() > 100) {
                    varErrors.add("Each distractor must contain between 10 and 100 characters");
                }
            }
        }

        List<String> varAllAnswers = new ArrayList<>();

        if (varCorrectAnswer != null) varAllAnswers.add(varCorrectAnswer);

        if (varAnswers != null) varAllAnswers.addAll(varAnswers);

        long varDistinctCount = varAllAnswers.stream()
                .filter(varAnswer -> varAnswer != null)
                .map(varAnswer -> varAnswer.trim().toLowerCase())
                .distinct()
                .count();

        if (varDistinctCount != varAllAnswers.size()) varErrors.add("Answers must be unique");

        boolean varHasProhibitedAlternative = varAllAnswers.stream()
                .filter(varAnswer -> varAnswer != null)
                .map(varAnswer -> varAnswer.toLowerCase())
                .anyMatch(varAnswer ->
                        varAnswer.contains("todas las anteriores") || varAnswer.contains("ninguna de las anteriores"));

        if (varHasProhibitedAlternative) varErrors.add("Answers must not contain prohibited alternatives");

        if (!varErrors.isEmpty()) throw new IllegalArgumentException(String.join("; ", varErrors));
    }
}