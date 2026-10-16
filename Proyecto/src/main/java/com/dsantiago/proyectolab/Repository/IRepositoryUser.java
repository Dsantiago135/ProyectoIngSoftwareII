package com.dsantiago.proyectolab.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

public interface IRepositoryUser extends JpaRepository<clsUser, String> {

    /**
     * Returns the role assigned to a user.
     * @param prmUsername username to look up
     * @return stored user role
     */
    default EnumRole opGetRole(String prmUsername) {
        return opFindByUsername(prmUsername).getAttRole();
    }

    /**
     * Finds a user by its username.
     * @param prmUsername username to look up
     * @return matching user
     */
    default clsUser opFindByUsername(String prmUsername) {
        return findById(prmUsername).orElseThrow(() -> new java.util.NoSuchElementException("User not found: " + prmUsername));
    }
}