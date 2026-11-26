package co.edu.unicauca.saberpro.question.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmContextType;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmDifficulty;
import co.edu.unicauca.saberpro.question.domain.model.clsQuestion.enmQuestionStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class clsQuestionEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID authorId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enmContextType contextType;
    @Column(nullable = false, columnDefinition = "text")
    private String context;
    private String mediaUrl;
    @Column(nullable = false, columnDefinition = "text")
    private String directQuestion;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_distractors", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "position")
    @Column(name = "value", nullable = false, columnDefinition = "text")
    private List<String> distractors = new ArrayList<>();
    @Column(nullable = false, columnDefinition = "text")
    private String correctAnswer;
    @Column(nullable = false, columnDefinition = "text")
    private String justification;
    @Column(columnDefinition = "text")
    private String bibliography;
    private String competence;
    private String topic;
    private String subtopic;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enmDifficulty difficulty;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enmQuestionStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected clsQuestionEntity() {
    }

    public clsQuestionEntity(UUID prmId, UUID prmAuthorId, enmContextType prmContextType,
                             String prmContext, String prmMediaUrl, String prmDirectQuestion,
                             List<String> prmDistractors, String prmCorrectAnswer, String prmJustification,
                             String prmBibliography, String prmCompetence, String prmTopic, String prmSubtopic,
                             enmDifficulty prmDifficulty, enmQuestionStatus prmStatus,
                             Instant prmCreatedAt, Instant prmUpdatedAt) {
        id = prmId;
        authorId = prmAuthorId;
        contextType = prmContextType;
        context = prmContext;
        mediaUrl = prmMediaUrl;
        directQuestion = prmDirectQuestion;
        distractors = new ArrayList<>(prmDistractors);
        correctAnswer = prmCorrectAnswer;
        justification = prmJustification;
        bibliography = prmBibliography;
        competence = prmCompetence;
        topic = prmTopic;
        subtopic = prmSubtopic;
        difficulty = prmDifficulty;
        status = prmStatus;
        createdAt = prmCreatedAt;
        updatedAt = prmUpdatedAt;
    }

    public UUID getId() { return id; }
    public UUID getAuthorId() { return authorId; }
    public enmContextType getContextType() { return contextType; }
    public String getContext() { return context; }
    public String getMediaUrl() { return mediaUrl; }
    public String getDirectQuestion() { return directQuestion; }
    public List<String> getDistractors() { return List.copyOf(distractors); }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getJustification() { return justification; }
    public String getBibliography() { return bibliography; }
    public String getCompetence() { return competence; }
    public String getTopic() { return topic; }
    public String getSubtopic() { return subtopic; }
    public enmDifficulty getDifficulty() { return difficulty; }
    public enmQuestionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}