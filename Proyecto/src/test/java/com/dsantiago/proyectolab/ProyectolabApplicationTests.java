package com.dsantiago.proyectolab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.dsantiago.proyectolab.Application.pkgServices.IServiceQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

/** Spring composition smoke test using the isolated H2 test datasource. */
@SpringBootTest
class ProyectolabApplicationTests {

    @Autowired
    private IRepositoryUser userRepository;

    @Autowired
    private IServiceQuestion questionService;

    @PersistenceContext
    private EntityManager entityManager;

    /** Confirms that application services, repositories, plugins, and JPA start together. */
    @Test
    void contextLoads() {
    }

    /** TEST-04-01: persists a complete question linked to its existing author. */
    @Test
    @Transactional
    void crearPregunta_ConEstructuraCompleta_DebeRegistrar() {
        String username = "author-" + java.util.UUID.randomUUID();
        userRepository.save(new clsUser(username, "encoded", "Author", username + "@example.edu",
            EnumRole.QUESTIONAUTHOR));
        rqtQuestion request = new rqtQuestion(
            "This context contains enough characters to satisfy the agreed minimum length rule.",
            "Which answer is correct?",
            java.util.List.of("First distractor answer", "Second distractor answer",
                "Third distractor answer", "Fourth distractor answer"),
            "The correct answer", "Bibliography reference", "Competence", "Topic",
            EnumDificulty.MEDIUM, "MULTIPLE_CHOICE", username);

        clsQuestion created = questionService.opCreateQuestion(request);
        entityManager.flush();
        entityManager.clear();
        clsQuestion persisted = entityManager.find(clsQuestion.class, created.getAttId());

        org.junit.jupiter.api.Assertions.assertEquals(request.attQuestion(), persisted.getAttQuestion());
        org.junit.jupiter.api.Assertions.assertEquals(EnumRole.QUESTIONAUTHOR,
            persisted.getAttAuthor().getAttRole());
    }
}