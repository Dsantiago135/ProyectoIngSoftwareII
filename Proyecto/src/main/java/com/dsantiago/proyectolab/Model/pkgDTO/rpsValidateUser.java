package com.dsantiago.proyectolab.Model.pkgDTO;

import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

public record rpsValidateUser(String attUsername,String attName,EnumRole attRole) {

    /**
     * Converts a User entity into an rpsValidateUser.
     * @param prmUser User entity to convert.
     */
    public rpsValidateUser(clsUser prmUser) {
        this(
                prmUser.getAttUsername(),
                prmUser.getAttName(),
                prmUser.getAttRole()
        );
    }
}