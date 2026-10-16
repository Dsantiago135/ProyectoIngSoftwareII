package com.dsantiago.proyectolab.UI;

import java.util.List;

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

public interface IAppController {

    /**
     * Authenticates a user.
     * @param prmRequest login credentials
     * @return authenticated user details
     */
    rpsValidateUser opLogin(rqtValidateUser prmRequest);

    /**
     * Registers a student.
     * @param prmRequest student data
     * @return public student details
     */
    rpsUser opCreateStudent(rqtStudent prmRequest);

    /**
     * Registers a user as an administrator.
     * @param prmRequest user data
     * @return public user details
     */
    rpsUser opCreateUser(rqtUser prmRequest);

    /**
     * Updates a user's details.
     * @param prmRequest updated user data
     * @return public user details
     */
    rpsUser opUpdateUser(rqtUser prmRequest);

    /**
     * Changes a user's role.
     * @param prmUsername username to modify
     * @param prmRole new role
     */
    void opModifyRole(String prmUsername, EnumRole prmRole);

    /**
     * Retrieves one user by username.
     * @param prmUsername username to look up
     * @return public user details
     */
    rpsUser opGetUser(String prmUsername);

    /**
     * Retrieves all users.
     * @return public user details
     */
    List<rpsUser> opGetAllUsers();

    /**
     * Creates a question.
     * @param prmRequest complete question data
     * @return created question details
     */
    rpsQuestion opCreateQuestion(rqtQuestion prmRequest);

    /**
     * Updates a draft question.
     * @param prmRequest updated question data
     * @return updated question details
     */
    rpsQuestion opUpdateQuestion(rqtUpdateQuestion prmRequest);

    /**
     * Assigns question reviewers.
     * @param prmRequest question and reviewer usernames
     * @return updated question details
     */
    rpsQuestion opAssignReviewer(rqtAssignReviewer prmRequest);

    /**
     * Changes a question's status.
     * @param prmRequest question id and requested status
     * @return updated question details
     */
    rpsQuestion opChangeStatus(rqtChangeStatus prmRequest);

    /**
     * Retrieves questions for an author.
     * @param prmUsername author username
     * @return questions authored by the user
     */
    List<rpsQuestion> opGetQuestionsByAuthor(String prmUsername);
}