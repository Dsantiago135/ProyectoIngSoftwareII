package com.dsantiago.proyectolab.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dsantiago.proyectolab.Model.pkgModels.clsQuestion;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;

public interface IRepositoryQuestion extends JpaRepository<clsQuestion, Integer> {

    /**
     * Finds questions authored by the supplied user.
     * @param prmAuthor question author
     * @return authored questions
     */
    @Query("select question from clsQuestion question where question.attAuthor = :prmAuthor")
    List<clsQuestion> opFindByAuthor(@Param("prmAuthor") clsUser prmAuthor);
}
