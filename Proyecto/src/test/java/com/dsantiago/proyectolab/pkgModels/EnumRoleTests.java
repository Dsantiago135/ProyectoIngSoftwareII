package com.dsantiago.proyectolab.pkgModels;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;

class EnumRoleTests {

    @Test
    void roles_ContainsFixedCatalog() {
        assertArrayEquals(new EnumRole[] {
            EnumRole.ADMINISTRATOR,
            EnumRole.STUDENT,
            EnumRole.TEACHER,
            EnumRole.REVIEWER,
            EnumRole.QUESTIONAUTHOR
        }, EnumRole.values());
    }
}