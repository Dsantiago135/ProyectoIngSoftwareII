package com.dsantiago.proyectolab.pkgServices;

import com.dsantiago.proyectolab.Application.pkgMicroKernel.clsMicroKernel;
import com.dsantiago.proyectolab.Application.pkgServices.clsServicesQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUpdateQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtAssignReviewer;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtChangeStatus;
import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumDificulty;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.EnumStatus;
import com.dsantiago.proyectolab.Repository.IRepositoryQuestion;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class clsServicesQuestionTests {
    @Test
    void crearPregunta_Valida_DebeRetornarPregunta() {
        clsMicroKernel varMicroKernel = mock(clsMicroKernel.class);
        IRepositoryQuestion varQuestionRepository =
                mock(IRepositoryQuestion.class);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsQuestion varQuestion = questionInState(EnumStatus.DRAFT);
        clsUser varAuthor = createUser(
                "author",
                EnumRole.QUESTIONAUTHOR
        );

        when(varMicroKernel.opCreateQuestion(any(rqtQuestion.class)))
                .thenReturn(varQuestion);

        when(varUserRepository.findById("author"))
                .thenReturn(Optional.of(varAuthor));

        clsServicesQuestion varService = new clsServicesQuestion(
                varMicroKernel,
                varQuestionRepository,
                varUserRepository
        );

        clsQuestion varCreated = varService.opCreateQuestion(
                validQuestionRequest()
        );

        assertNotNull(varCreated);
        assertEquals("Question", varCreated.getAttQuestion());
        assertEquals(varAuthor, varCreated.getAttAuthor());

        verify(varMicroKernel).opCreateQuestion(any(rqtQuestion.class));
        verify(varUserRepository).findById("author");
    }

    @Test
    void crearPregunta_ContextoInvalido_DebeLanzarExcepcion() {
        clsMicroKernel varMicroKernel = mock(clsMicroKernel.class);
        IRepositoryQuestion varQuestionRepository =
                mock(IRepositoryQuestion.class);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                varMicroKernel,
                varQuestionRepository,
                varUserRepository
        );

        rqtQuestion varRequest = new rqtQuestion(
                "",
                "Question",
                List.of("Answer A", "Answer B", "Answer C", "Answer D"),
                "Answer A",
                "Bibliography",
                "Topic",
                "Subtopic",
                EnumDificulty.MEDIUM,
                "MULTIPLE_CHOICE",
                "author"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> varService.opCreateQuestion(varRequest)
        );

        verifyNoInteractions(varMicroKernel);
        verifyNoInteractions(varUserRepository);
    }

    // --------------------------------------------------
    // UPDATE QUESTION
    // --------------------------------------------------

    @Test
    void actualizarPregunta_DraftValido_DebeGuardarCambios() {
        clsQuestion varQuestion = questionInState(EnumStatus.DRAFT);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtUpdateQuestion varRequest = validUpdateRequest();

        clsQuestion varUpdated = varService.opUpdateQuestion(varRequest);

        assertNotNull(varUpdated);
        assertEquals("Updated context", varUpdated.getAttContext());
        assertEquals("Updated question", varUpdated.getAttQuestion());
        assertEquals("Option A", varUpdated.getAttAnswer1());
        assertEquals("Option B", varUpdated.getAttAnswer2());
        assertEquals("Option C", varUpdated.getAttAnswer3());
        assertEquals("Option D", varUpdated.getAttAnswer4());
        assertEquals("Option A", varUpdated.getAttCorrectAnswer());

        verify(varQuestionRepository).save(varQuestion);
    }

    @Test
    void actualizarPregunta_NoEstaEnDraft_DebeLanzarExcepcion() {
        clsQuestion varQuestion = questionInState(EnumStatus.APPROVED);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        assertThrows(
                IllegalStateException.class,
                () -> varService.opUpdateQuestion(validUpdateRequest())
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    @Test
    void actualizarPregunta_NoExiste_DebeLanzarExcepcion() {
        IRepositoryQuestion varQuestionRepository =
                mock(IRepositoryQuestion.class);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        when(varQuestionRepository.findById(1))
                .thenReturn(Optional.empty());

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> varService.opUpdateQuestion(validUpdateRequest())
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    // --------------------------------------------------
    // ASSIGN REVIEWERS
    // --------------------------------------------------

    @Test
    void asignarVerificadores_AsignacionValida_DebeGuardarCambios() {
        clsQuestion varQuestion =
                questionInState(EnumStatus.PENDING_REVIEW);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsUser varReviewer1 = createUser(
                "reviewer1",
                EnumRole.REVIEWER
        );

        clsUser varReviewer2 = createUser(
                "reviewer2",
                EnumRole.REVIEWER
        );

        when(varUserRepository.findById("reviewer1"))
                .thenReturn(Optional.of(varReviewer1));

        when(varUserRepository.findById("reviewer2"))
                .thenReturn(Optional.of(varReviewer2));

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtAssignReviewer varRequest = new rqtAssignReviewer(
                1,
                "reviewer1",
                "reviewer2"
        );

        clsQuestion varUpdated = varService.opAssignReviewer(varRequest);

        assertEquals(EnumStatus.UNDER_REVIEW, varUpdated.getAttStatus());
        assertEquals(varReviewer1, varUpdated.getAttReviewer1());
        assertEquals(varReviewer2, varUpdated.getAttReviewer2());

        verify(varQuestionRepository).save(varQuestion);
    }

    @Test
    void asignarVerificadores_SinVerificadorPrincipal_DebeLanzarExcepcion() {
        clsQuestion varQuestion =
                questionInState(EnumStatus.PENDING_REVIEW);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtAssignReviewer varRequest = new rqtAssignReviewer(
                1,
                " ",
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> varService.opAssignReviewer(varRequest)
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    @Test
    void asignarVerificadores_EstadoIncorrecto_DebeLanzarExcepcion() {
        clsQuestion varQuestion =
                questionInState(EnumStatus.DRAFT);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtAssignReviewer varRequest = new rqtAssignReviewer(
                1,
                "reviewer1",
                null
        );

        assertThrows(
                IllegalStateException.class,
                () -> varService.opAssignReviewer(varRequest)
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    // --------------------------------------------------
    // QUERY QUESTIONS BY AUTHOR
    // --------------------------------------------------

    @Test
    void consultarPreguntasPorAutor_DebeRetornarResultados() {
        clsQuestion varQuestion = questionInState(EnumStatus.DRAFT);

        IRepositoryQuestion varQuestionRepository =
                mock(IRepositoryQuestion.class);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        when(varQuestionRepository.opFindByAuthor(any(clsUser.class)))
                .thenReturn(List.of(varQuestion));

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        List<clsQuestion> varResults =
                varService.opGetQuestionByAuthor("author");

        assertNotNull(varResults);
        assertEquals(1, varResults.size());
        assertEquals(varQuestion, varResults.get(0));

        verify(varQuestionRepository).opFindByAuthor(any(clsUser.class));
    }

    // --------------------------------------------------
    // CHANGE QUESTION STATUS
    // --------------------------------------------------

    @Test
    void cambiarEstado_DraftAPendingReview_DebeGuardarCambio() {
        clsQuestion varQuestion = questionInState(EnumStatus.DRAFT);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtChangeStatus varRequest = new rqtChangeStatus(
                1,
                EnumStatus.PENDING_REVIEW
        );

        clsQuestion varUpdated = varService.opChangeStatus(varRequest);

        assertEquals(
                EnumStatus.PENDING_REVIEW,
                varUpdated.getAttStatus()
        );

        verify(varQuestionRepository).save(varQuestion);
    }

    @Test
    void cambiarEstado_TransicionInvalida_DebeLanzarExcepcion() {
        clsQuestion varQuestion = questionInState(EnumStatus.DRAFT);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtChangeStatus varRequest = new rqtChangeStatus(
                1,
                EnumStatus.APPROVED
        );

        assertThrows(
                IllegalStateException.class,
                () -> varService.opChangeStatus(varRequest)
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    @Test
    void cambiarEstado_PreguntaFiled_DebeImpedirTransicion() {
        clsQuestion varQuestion = questionInState(EnumStatus.FILED);

        IRepositoryQuestion varQuestionRepository =
                repositoryFor(varQuestion);
        IRepositoryUser varUserRepository =
                mock(IRepositoryUser.class);

        clsServicesQuestion varService = new clsServicesQuestion(
                null,
                varQuestionRepository,
                varUserRepository
        );

        rqtChangeStatus varRequest = new rqtChangeStatus(
                1,
                EnumStatus.DRAFT
        );

        assertThrows(
                IllegalStateException.class,
                () -> varService.opChangeStatus(varRequest)
        );

        verify(varQuestionRepository, never()).save(any(clsQuestion.class));
    }

    @Test
    void enumStatus_DebeContenerSieteEstados() {
        assertEquals(7, EnumStatus.values().length);
    }

    // --------------------------------------------------
    // TEST DATA HELPERS
    // --------------------------------------------------

    private rqtQuestion validQuestionRequest() {
        return new rqtQuestion(
                "Context",
                "Question",
                List.of("Answer A", "Answer B", "Answer C", "Answer D"),
                "Answer A",
                "Bibliography",
                "Topic",
                "Subtopic",
                EnumDificulty.MEDIUM,
                "MULTIPLE_CHOICE",
                "author"
        );
    }

    private rqtUpdateQuestion validUpdateRequest() {
        return new rqtUpdateQuestion(
                1,"Updated context","Updated question",
                List.of("Option A", "Option B", "Option C", "Option D"),
                "Option A","Updated bibliography",
                "Updated topic","Updated subtopic",EnumDificulty.MEDIUM);
    }

    private clsQuestion questionInState(EnumStatus prmStatus) {
        clsQuestion varQuestion = new clsQuestion();

        varQuestion.setAttId(1);
        varQuestion.setAttContext("Context");
        varQuestion.setAttQuestion("Question");
        varQuestion.setAttAnswer1("Answer A");
        varQuestion.setAttAnswer2("Answer B");
        varQuestion.setAttAnswer3("Answer C");
        varQuestion.setAttAnswer4("Answer D");
        varQuestion.setAttCorrectAnswer("Answer A");
        varQuestion.setAttBibliography("Bibliography");
        varQuestion.setAttTopic("Topic");
        varQuestion.setAttSubTopic("Subtopic");
        varQuestion.setAttDificulty(EnumDificulty.MEDIUM);
        varQuestion.setAttStatus(prmStatus);
        varQuestion.setAttAuthor(
                createUser("author", EnumRole.QUESTIONAUTHOR)
        );
        varQuestion.setAttType("MULTIPLE_CHOICE");

        return varQuestion;
    }

    private clsUser createUser(
            String prmUsername,
            EnumRole prmRole
    ) {
        return new clsUser(
                prmUsername,
                "hashed-password",
                prmUsername,
                prmUsername + "@example.edu",
                prmRole
        );
    }

    private IRepositoryQuestion repositoryFor(
            clsQuestion prmQuestion
    ) {
        IRepositoryQuestion varRepository =
                mock(IRepositoryQuestion.class);

        when(varRepository.findById(prmQuestion.getAttId()))
                .thenReturn(Optional.of(prmQuestion));

        when(varRepository.save(any(clsQuestion.class)))
                .thenAnswer(varInvocation -> varInvocation.getArgument(0));

        return varRepository;
    }
}