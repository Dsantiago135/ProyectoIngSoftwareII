package com.dsantiago.proyectolab.Model.pkgDTO;

import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

public record rpsUser(String attUsername, String attName, String attEmail, EnumRole attRole) {
    
    /**
    * Converts a User entity into an rpsUser.
    * @param prmUser User entity to convert.
    */
    public rpsUser(clsUser prmUser) {
        this(
                prmUser.getAttUsername(),
                prmUser.getAttName(),
                prmUser.getAttEmail(),
                prmUser.getAttRole()
        );
    }

    /**
     * Converts an rpsUser into a User entity.
     * @return User entity created from this rpsUser.
     */
    public clsUser toUser() {
        clsUser varUser = new clsUser();

        varUser.setAttUsername(attUsername);
        varUser.setAttName(attName);
        varUser.setAttEmail(attEmail);
        varUser.setAttRole(attRole);

        return varUser;
    }
}