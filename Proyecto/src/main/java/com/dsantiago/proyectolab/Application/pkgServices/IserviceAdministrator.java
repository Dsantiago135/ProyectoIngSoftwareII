package com.dsantiago.proyectolab.Application.pkgServices;

import java.util.List;

import com.dsantiago.proyectolab.Model.pkgDTO.rqtUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

/** Administrative user operations defined by the class diagram. */
public interface IserviceAdministrator {

    /**
     * Creates a user with an encoded password.
     * @param prmUser user details
     * @return persisted user
     */
    clsUser opCreateUser(rqtUser prmUser);

    /**
     * Updates the stored details for a user.
     * @param prmUser updated user details
     * @return updated user
     */
    clsUser opUpdateUser(rqtUser prmUser);

    /**
     * Changes a user's fixed role.
     * @param prmUsername username to update
     * @param prmRole new role
     */
    void opModifyRole(String prmUsername, EnumRole prmRole);

    /**
     * Returns all persisted users.
     * @return stored users
     */
    List<clsUser> opGetAllUsers();
}
