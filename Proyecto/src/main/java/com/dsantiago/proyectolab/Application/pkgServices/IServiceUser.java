package com.dsantiago.proyectolab.Application.pkgServices;

import com.dsantiago.proyectolab.Model.pkgDTO.rpsValidateUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtStudent;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtValidateUser;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

public interface IServiceUser {

    /**
     * Authenticates a user using the supplied credentials.
     * @param prmRequest login credentials
     * @return authenticated user details
     */
    rpsValidateUser opValidateUser(rqtValidateUser prmRequest);

    /**
     * Registers a student with the fixed student role.
     * @param prmStudent student registration data
     * @return persisted student
     */
    clsUser opCreateStudent(rqtStudent prmStudent);

    /**
     * Looks up a user by username.
     * @param prmUsername username to look up
     * @return matching user
     */
    clsUser opGetUser(String prmUsername);
}
