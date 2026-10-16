package com.dsantiago.proyectolab.Application.pkgServices;

import org.springframework.stereotype.Service;

import com.dsantiago.proyectolab.Model.pkgDTO.rpsValidateUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtStudent;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtValidateUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class clsServiceUser implements IServiceUser {

    private final IServiceSecurity attIServiceSecurity;
    private final IRepositoryUser attIRepositoryUser;

    public clsServiceUser (IServiceSecurity prmIServiceSecurity, IRepositoryUser prmIRepositoryUser){
        this.attIServiceSecurity = prmIServiceSecurity;
        this.attIRepositoryUser = prmIRepositoryUser;
    }

    /** {@inheritDoc} */
    @Override
    public rpsValidateUser opValidateUser(rqtValidateUser prmRequest) {
        clsUser varUser = attIRepositoryUser.opFindByUsername(prmRequest.attUsername());
        if (!attIServiceSecurity.opValidatePassword(prmRequest.attPassword(), varUser.getAttPassword())) {
            log.warn("Invalid login attempt for user: {}", prmRequest.attUsername());
            throw new IllegalArgumentException("Invalid username or password");
        }
        log.info("User authenticated successfully: {}", prmRequest.attUsername());
        return new rpsValidateUser(varUser.getAttUsername(), varUser.getAttName(), varUser.getAttRole());
    }

    /** {@inheritDoc} */
    @Override
    public clsUser opCreateStudent(rqtStudent prmStudent) {
        if (prmStudent == null || prmStudent.attUsername() == null || prmStudent.attUsername().isBlank()
            || prmStudent.attName() == null || prmStudent.attName().isBlank()
            || prmStudent.attEmail() == null
            || !prmStudent.attEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
            || prmStudent.attPassword() == null || prmStudent.attPassword().length() < 8
            || !prmStudent.attPassword().matches(".*[A-Z].*")
            || !prmStudent.attPassword().matches(".*\\d.*")
            || !prmStudent.attPassword().matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Required student data is invalid");
        }

        if (attIRepositoryUser.existsById(prmStudent.attUsername())) {
            log.warn("Username already registered: {}", prmStudent.attUsername());
            throw new IllegalArgumentException("Username registered");
        }

        if (attIRepositoryUser.findAll().stream()
            .anyMatch(varUser -> prmStudent.attEmail().equalsIgnoreCase(varUser.getAttEmail()))) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        clsUser varUser = new clsUser(prmStudent.attUsername(),
            attIServiceSecurity.opEncryptPassword(prmStudent.attPassword()),
            prmStudent.attName(), prmStudent.attEmail(), EnumRole.STUDENT);
        clsUser varSavedUser = attIRepositoryUser.save(varUser);
        log.info("Student account created: {}", varSavedUser.getAttUsername());
        return varSavedUser;
    }

    /**
     * Finds a user by username.
     * @param prmUsername username to look up
     * @return matching user
     */
    @Override
    public clsUser opGetUser(String prmUsername) {
        return attIRepositoryUser.opFindByUsername(prmUsername);
    }
}
