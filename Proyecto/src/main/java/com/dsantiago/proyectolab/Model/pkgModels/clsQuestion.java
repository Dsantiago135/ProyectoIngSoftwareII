package com.dsantiago.proyectolab.Model.pkgModels;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblQuestion")
public class clsQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionId", nullable = false, unique = true)
    private int attId;

    @Column(name = "context", nullable = false, length = 750)
    private String attContext;

    @Column(name = "question", nullable = false, length = 500)
    private String attQuestion;

    @Column(name = "answer1", nullable = true, length = 100)
    private String attAnswer1;

    @Column(name = "answer2", nullable = true, length = 100)
    private String attAnswer2;

    @Column(name = "answer3", nullable = true, length = 100)
    private String attAnswer3;

    @Column(name = "answer4", nullable = true, length = 100)
    private String attAnswer4;

    @Column(name = "correctAnswer", nullable = true, length = 100)
    private String attCorrectAnswer;

    @Column(name = "bibliography", nullable = true, length = 500)
    private String attBibliography;

    @Column(name = "topic", nullable = true)
    private String attTopic;

    @Column(name = "subTopic", nullable = true)
    private String attSubTopic;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private EnumDificulty attDificulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnumStatus attStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorUsername", nullable = false)
    private clsUser attAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer1Username")
    private clsUser attReviewer1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer2Username")
    private clsUser attReviewer2;

    @Column(name = "questionType", nullable = false, length = 50)
    private String attType;
}