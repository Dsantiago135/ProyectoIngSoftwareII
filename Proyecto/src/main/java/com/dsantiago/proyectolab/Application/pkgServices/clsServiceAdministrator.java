package com.dsantiago.proyectolab.Application.pkgServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service 
public class clsServiceAdministrator implements IserviceAdministrator {
    private final IRepositoryUser attIUserRepository;
    private final IServiceSecurity attISecurityService;

    public clsServiceAdministrator(IRepositoryUser prmIUserRepository, IServiceSecurity prmISecurityService) {
        this.attIUserRepository = prmIUserRepository;
        this.attISecurityService = prmISecurityService;
    }

    /** {@inheritDoc} */
    @Override
    public clsUser opCreateUser(rqtUser prmUser) {
        if (prmUser.attUsername() == null || prmUser.attUsername().isBlank()
            || prmUser.attName() == null || prmUser.attName().isBlank()
            || prmUser.attRole() == null
            || prmUser.attEmail() == null
            || !prmUser.attEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
            || prmUser.attPassword() == null || prmUser.attPassword().length() < 8
            || !prmUser.attPassword().matches(".*[A-Z].*")
            || !prmUser.attPassword().matches(".*\\d.*")
            || !prmUser.attPassword().matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Required user data is invalid");
        }

        if (attIUserRepository.existsById(prmUser.attUsername())) {
            log.warn("Username already registered: {}", prmUser.attUsername());
            throw new IllegalArgumentException("Username registered");
        }

        if (attIUserRepository.findAll().stream()
            .anyMatch(varUser -> prmUser.attEmail().equalsIgnoreCase(varUser.getAttEmail()))) {
            throw new IllegalArgumentException("Email already registered");
        }

        clsUser varUser = new clsUser(prmUser.attUsername(),
            attISecurityService.opEncryptPassword(prmUser.attPassword()),
            prmUser.attName(), prmUser.attEmail(), prmUser.attRole());
        clsUser varSavedUser = attIUserRepository.save(varUser);
        log.info("User account created: {}", varSavedUser.getAttUsername());
        return varSavedUser;
    }

    /** {@inheritDoc} */
    @Override
    public clsUser opUpdateUser(rqtUser prmUser) {
        if (prmUser.attName() == null || prmUser.attName().isBlank()
            || prmUser.attRole() == null || prmUser.attEmail() == null
            || !prmUser.attEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Required user data is invalid");
        }

        clsUser varUser = attIUserRepository.opFindByUsername(prmUser.attUsername());
        if (attIUserRepository.findAll().stream()
            .anyMatch(varExistingUser -> !varExistingUser.getAttUsername().equals(prmUser.attUsername())
                && prmUser.attEmail().equalsIgnoreCase(varExistingUser.getAttEmail()))) {
            throw new IllegalArgumentException("Email already registered");
        }
        varUser.setAttName(prmUser.attName());
        varUser.setAttEmail(prmUser.attEmail());
        varUser.setAttRole(prmUser.attRole());
        if (prmUser.attPassword() != null && !prmUser.attPassword().isBlank()) {
            varUser.setAttPassword(attISecurityService.opEncryptPassword(prmUser.attPassword()));
        }
        log.info("User account updated: {}", prmUser.attUsername());
        return attIUserRepository.save(varUser);
    }

    /** {@inheritDoc} */
    @Override
    public void opModifyRole(String prmUsername, EnumRole prmRole) {
        clsUser varUser = attIUserRepository.opFindByUsername(prmUsername);
        varUser.setAttRole(prmRole);
        attIUserRepository.save(varUser);
        log.info("Role updated for user: {}", prmUsername);
    }

    /** {@inheritDoc} */
    @Override
    public List<clsUser> opGetAllUsers() {
        return attIUserRepository.findAll();
    }

}