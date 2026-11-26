package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.clsDomainException;
import co.edu.unicauca.saberpro.question.domain.exception.clsAuthorizationException;
import co.edu.unicauca.saberpro.question.domain.exception.clsConflictException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class clsQuestion {

    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final List<String> FORBIDDEN_PHRASES = List.of("todas las anteriores", "ninguna de las anteriores");

    public enum enmQuestionStatus {BORRADOR,PENDIENTE_REVISION,EN_REVISION,APROBADA,RECHAZADA,PUBLICADA,ARCHIVADA}
    public enum enmDifficulty {BAJA,MEDIA,ALTA}    
    public enum enmContextType {MULTIPLE_CHOICE,CASE,MULTIMEDIA}
    
    private final UUID attId;
    private final UUID attAuthorId;
    private final enmContextType attContextType;
    private final String attContext;
    private final String attMediaUrl;
    private final String attDirectQuestion;
    private final List<String> attDistractors;
    private final String attCorrectAnswer;
    private final String attJustification;
    private final String attBibliography;
    private final String attCompetence;
    private final String attTopic;
    private final String attSubtopic;
    private final enmDifficulty attDifficulty;
    private final Instant attCreatedAt;
    private enmQuestionStatus attStatus;
    private Instant attUpdatedAt;

    private clsQuestion(UUID prmId, UUID prmAuthorId, clsQuestionDraft prmDraft,
                        enmQuestionStatus prmStatus, Instant prmCreatedAt, Instant prmUpdatedAt) {
        attId = Objects.requireNonNull(prmId);
        attAuthorId = Objects.requireNonNull(prmAuthorId);
        attContextType = Objects.requireNonNull(prmDraft.contextType());
        attContext = opTrim(prmDraft.context());
        attMediaUrl = opTrim(prmDraft.mediaUrl());
        attDirectQuestion = opTrim(prmDraft.directQuestion());
        attDistractors = prmDraft.distractors() == null ? List.of()
                : prmDraft.distractors().stream().map(clsQuestion::opTrim).toList();
        attCorrectAnswer = opTrim(prmDraft.correctAnswer());
        attJustification = opTrim(prmDraft.justification());
        attBibliography = opTrim(prmDraft.bibliography());
        attCompetence = opTrim(prmDraft.competence());
        attTopic = opTrim(prmDraft.topic());
        attSubtopic = opTrim(prmDraft.subtopic());
        attDifficulty = Objects.requireNonNull(prmDraft.difficulty());
        attStatus = Objects.requireNonNull(prmStatus);
        attCreatedAt = Objects.requireNonNull(prmCreatedAt);
        attUpdatedAt = Objects.requireNonNull(prmUpdatedAt);
    }

    public static clsQuestion opCreate(UUID prmAuthorId, clsQuestionDraft prmDraft) {
        if (prmAuthorId == null || prmDraft == null || prmDraft.contextType() == null || prmDraft.difficulty() == null) {
            throw new clsDomainException("Autor, tipo de contexto y dificultad son obligatorios");
        }
        Instant now = Instant.now();
        clsQuestion question = new clsQuestion(UUID.randomUUID(), prmAuthorId, prmDraft,
                enmQuestionStatus.BORRADOR, now, now);
        question.opValidate();
        return question;
    }

    public static clsQuestion opRebuild(UUID prmId, UUID prmAuthorId, clsQuestionDraft prmDraft,
                                        enmQuestionStatus prmStatus, Instant prmCreatedAt, Instant prmUpdatedAt) {
        clsQuestion question = new clsQuestion(prmId, prmAuthorId, prmDraft, prmStatus, prmCreatedAt, prmUpdatedAt);
        question.opValidate();
        return question;
    }

    public void opValidate() {
        opRequireText(attContext, "El contexto es obligatorio");
        opRequireText(attDirectQuestion, "La pregunta directa es obligatoria");
        opRequireText(attCorrectAnswer, "La respuesta correcta es obligatoria");
        opRequireText(attJustification, "La justificación es obligatoria");
        if (attDistractors.size() != 4) {
            throw new clsDomainException("La pregunta debe tener exactamente cuatro distractores");
        }
        attDistractors.forEach(value -> opRequireText(value, "Los distractores no pueden estar vacíos"));
        List<String> normalizedDistractors = attDistractors.stream().map(clsQuestion::opNormalize).toList();
        if (normalizedDistractors.stream().distinct().count() != 4) {
            throw new clsDomainException("Los cuatro distractores deben ser distintos");
        }
        String normalizedCorrectAnswer = opNormalize(attCorrectAnswer);
        if (normalizedDistractors.contains(normalizedCorrectAnswer)) {
            throw new clsDomainException("La respuesta correcta debe ser distinta de los distractores");
        }
        if (attContextType == enmContextType.MULTIMEDIA && attMediaUrl == null) {
            throw new clsDomainException("Las preguntas multimedia requieren una URL de recurso");
        }
        if (attContextType != enmContextType.MULTIMEDIA && attMediaUrl != null) {
            throw new clsDomainException("La URL de recurso solo aplica a preguntas multimedia");
        }
        List<String> options = new java.util.ArrayList<>(attDistractors);
        options.add(attCorrectAnswer);
        if (options.stream().map(clsQuestion::opNormalize).anyMatch(clsQuestion::opContainsForbiddenPhrase)) {
            throw new clsDomainException("No se permiten las expresiones 'Todas las anteriores' ni 'Ninguna de las anteriores'");
        }
    }

    public void opSubmit(UUID prmActorId) {
        if (!attAuthorId.equals(prmActorId)) {
            throw new clsAuthorizationException("Solo el autor puede enviar su pregunta a revisión");
        }
        if (attStatus != enmQuestionStatus.BORRADOR) {
            throw new clsConflictException("Solo una pregunta en BORRADOR puede enviarse a revisión");
        }
        opValidate();
        attStatus = enmQuestionStatus.PENDIENTE_REVISION;
        attUpdatedAt = Instant.now();
    }

    public void opChangeStatus(enmQuestionStatus prmStatus) {
        boolean validTransition = (attStatus == enmQuestionStatus.PENDIENTE_REVISION
                && prmStatus == enmQuestionStatus.EN_REVISION)
                || (attStatus == enmQuestionStatus.EN_REVISION
                && (prmStatus == enmQuestionStatus.APROBADA || prmStatus == enmQuestionStatus.RECHAZADA));
        if (!validTransition) {
            throw new clsConflictException("La transición de estado solicitada no está permitida");
        }
        attStatus = prmStatus;
        attUpdatedAt = Instant.now();
    }

    private static String opTrim(String prmValue) {
        return prmValue == null || prmValue.isBlank() ? null : prmValue.trim();
    }

    private static void opRequireText(String prmValue, String prmMessage) {
        if (prmValue == null || prmValue.isBlank()) {
            throw new clsDomainException(prmMessage);
        }
    }

    private static String opNormalize(String prmValue) {
        String decomposed = Normalizer.normalize(prmValue, Normalizer.Form.NFD);
        return MARKS.matcher(decomposed).replaceAll("").toLowerCase(Locale.ROOT).trim();
    }

    private static boolean opContainsForbiddenPhrase(String prmValue) {
        return FORBIDDEN_PHRASES.stream().anyMatch(prmValue::contains);
    }

    public UUID getId() { return attId; }
    public UUID getAuthorId() { return attAuthorId; }
    public enmContextType getContextType() { return attContextType; }
    public String getContext() { return attContext; }
    public String getMediaUrl() { return attMediaUrl; }
    public String getDirectQuestion() { return attDirectQuestion; }
    public List<String> getDistractors() { return attDistractors; }
    public String getCorrectAnswer() { return attCorrectAnswer; }
    public String getJustification() { return attJustification; }
    public String getBibliography() { return attBibliography; }
    public String getCompetence() { return attCompetence; }
    public String getTopic() { return attTopic; }
    public String getSubtopic() { return attSubtopic; }
    public enmDifficulty getDifficulty() { return attDifficulty; }
    public enmQuestionStatus getStatus() { return attStatus; }
    public Instant getCreatedAt() { return attCreatedAt; }
    public Instant getUpdatedAt() { return attUpdatedAt; }
}