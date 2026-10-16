package com.dsantiago.proyectolab.UI;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dsantiago.proyectolab.Application.pkgServices.IServiceQuestion;
import com.dsantiago.proyectolab.Application.pkgServices.IServiceUser;
import com.dsantiago.proyectolab.Application.pkgServices.IserviceAdministrator;
import com.dsantiago.proyectolab.Model.pkgDTO.rpsQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rpsUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rpsValidateUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtAssignReviewer;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtChangeStatus;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtStudent;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUpdateQuestion;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtValidateUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class clsAppController implements IAppController {

    private final IServiceUser attIUserService;
    private final IserviceAdministrator attIAdministratorService;
    private final IServiceQuestion attIQuestionService;

    public clsAppController(IServiceUser prmIUserService,
        IserviceAdministrator prmIAdministratorService, IServiceQuestion prmIQuestionService) {
        this.attIUserService = prmIUserService;
        this.attIAdministratorService = prmIAdministratorService;
        this.attIQuestionService = prmIQuestionService;
    }

    /** {@inheritDoc} */
    @Override
    public rpsValidateUser opLogin(rqtValidateUser prmRequest) {
        return attIUserService.opValidateUser(prmRequest);
    }

    /** {@inheritDoc} */
    @Override
    public rpsUser opCreateStudent(rqtStudent prmRequest) {
        opValidatePasswordFormat(prmRequest.attPassword());
        return new rpsUser(attIUserService.opCreateStudent(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public rpsUser opCreateUser(rqtUser prmRequest) {
        opValidatePasswordFormat(prmRequest.attPassword());
        return new rpsUser(attIAdministratorService.opCreateUser(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public rpsUser opUpdateUser(rqtUser prmRequest) {
        return new rpsUser(attIAdministratorService.opUpdateUser(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public void opModifyRole(String prmUsername, EnumRole prmRole) {
        attIAdministratorService.opModifyRole(prmUsername, prmRole);
    }

    /** {@inheritDoc} */
    @Override
    public rpsUser opGetUser(String prmUsername) {
        return new rpsUser(attIUserService.opGetUser(prmUsername));
    }

    /** {@inheritDoc} */
    @Override
    public List<rpsUser> opGetAllUsers() {
        return attIAdministratorService.opGetAllUsers().stream()
            .map(varUser -> new rpsUser(varUser))
            .toList();
    }

    /** {@inheritDoc} */
    @Override
    public rpsQuestion opCreateQuestion(rqtQuestion prmRequest) {
        return new rpsQuestion(attIQuestionService.opCreateQuestion(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public rpsQuestion opUpdateQuestion(rqtUpdateQuestion prmRequest) {
        return new rpsQuestion(attIQuestionService.opUpdateQuestion(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public rpsQuestion opAssignReviewer(rqtAssignReviewer prmRequest) {
        return new rpsQuestion(attIQuestionService.opAssignReviewer(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public rpsQuestion opChangeStatus(rqtChangeStatus prmRequest) {
        return new rpsQuestion(attIQuestionService.opChangeStatus(prmRequest));
    }

    /** {@inheritDoc} */
    @Override
    public List<rpsQuestion> opGetQuestionsByAuthor(String prmUsername) {
        return attIQuestionService.opGetQuestionByAuthor(prmUsername).stream()
            .map(varQuestion -> new rpsQuestion(varQuestion))
            .toList();
    }

    /**
     * Enforces the password rules fixed by the functional document.
     * @param prmPassword password to validate
     * @throws IllegalArgumentException when the password is invalid
     */
    private void opValidatePasswordFormat(String prmPassword) {
        if (prmPassword == null || prmPassword.length() < 8
            || !prmPassword.matches(".*[A-Z].*")
            || !prmPassword.matches(".*\\d.*")
            || !prmPassword.matches(".*[^A-Za-z0-9].*")) {
            log.warn("Password does not meet the required format");
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters, uppercase, digit, and special character");
        }
    }
}
