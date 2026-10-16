package com.dsantiago.proyectolab.pkgServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.dsantiago.proyectolab.Application.pkgServices.IServiceSecurity;
import com.dsantiago.proyectolab.Application.pkgServices.clsServiceSecurity;
import com.dsantiago.proyectolab.Application.pkgServices.clsServiceUser;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtStudent;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtValidateUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

class clsServiceUserTests {

    /** Registers a student with a hashed password and fixed role. */
    @Test
    void opCreateStudent_StoresEncodedPasswordAndStudentRole() {
        IServiceSecurity security = new clsServiceSecurity();
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.save(any(clsUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        clsServiceUser service = new clsServiceUser(security, repository);

        clsUser created = service.opCreateStudent(
            new rqtStudent("student", "StrongPass1!", "Student Name", "student@example.edu"));

        assertEquals(EnumRole.STUDENT, created.getAttRole());
        assertEquals("student@example.edu", created.getAttEmail());
        assertEquals(false, security.opValidatePassword("OtherPass1!", created.getAttPassword()));
        verify(repository).save(any(clsUser.class));
    }

    /** Rejects duplicate usernames before password hashing or persistence. */
    @Test
    void opCreateStudent_RejectsDuplicateUsername() {
        IServiceSecurity security = mock(IServiceSecurity.class);
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.existsById("student")).thenReturn(true);
        clsServiceUser service = new clsServiceUser(security, repository);

        assertThrows(IllegalArgumentException.class, () -> service.opCreateStudent(
            new rqtStudent("student", "StrongPass1!", "Student Name", "student@example.edu")));

        verify(security, never()).opEncryptPassword(any());
        verify(repository, never()).save(any(clsUser.class));
    }

    /** TEST-01-03: rejects a malformed email without hashing or storing the account. */
    @Test
    void opCreateStudent_RejectsInvalidEmailWithoutSaving() {
        IServiceSecurity security = mock(IServiceSecurity.class);
        IRepositoryUser repository = mock(IRepositoryUser.class);
        clsServiceUser service = new clsServiceUser(security, repository);

        assertThrows(IllegalArgumentException.class, () -> service.opCreateStudent(
            new rqtStudent("student", "StrongPass1!", "Student Name", "invalid-email")));

        verify(security, never()).opEncryptPassword(any());
        verify(repository, never()).save(any(clsUser.class));
    }

    /** Authenticates a registered user and returns the UML response DTO. */
    @Test
    void opValidateUser_ReturnsAuthenticatedUserDetails() {
        IServiceSecurity security = new clsServiceSecurity();
        IRepositoryUser repository = mock(IRepositoryUser.class);
        String passwordHash = security.opEncryptPassword("StrongPass1!");
        when(repository.opFindByUsername("author")).thenReturn(
            new clsUser("author", passwordHash, "Author Name", "author@example.edu", EnumRole.QUESTIONAUTHOR));
        clsServiceUser service = new clsServiceUser(security, repository);

        var response = service.opValidateUser(new rqtValidateUser("author", "StrongPass1!"));

        assertEquals("author", response.attUsername());
        assertEquals("Author Name", response.attName());
        assertEquals(EnumRole.QUESTIONAUTHOR, response.attRole());
    }

    /** Rejects invalid credentials without returning account details. */
    @Test
    void opValidateUser_RejectsInvalidPassword() {
        IServiceSecurity security = new clsServiceSecurity();
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.opFindByUsername("author")).thenReturn(
            new clsUser("author", security.opEncryptPassword("StrongPass1!"),
                "Author Name", "author@example.edu", EnumRole.QUESTIONAUTHOR));
        clsServiceUser service = new clsServiceUser(security, repository);

        assertThrows(IllegalArgumentException.class,
            () -> service.opValidateUser(new rqtValidateUser("author", "WrongPass1!")));
    }
}