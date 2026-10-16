package com.dsantiago.proyectolab.pkgServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dsantiago.proyectolab.Application.pkgServices.IServiceSecurity;
import com.dsantiago.proyectolab.Application.pkgServices.clsServiceAdministrator;
import com.dsantiago.proyectolab.Application.pkgServices.clsServiceSecurity;
import com.dsantiago.proyectolab.Model.pkgDTO.rqtUser;
import com.dsantiago.proyectolab.Model.pkgModels.EnumRole;
import com.dsantiago.proyectolab.Model.pkgModels.clsUser;
import com.dsantiago.proyectolab.Repository.IRepositoryUser;

class clsServiceAdministratorTests {

    /** TEST-01-01 and TEST-02-03: creates a user with the requested role and a secure hash. */
    @Test
    void opCreateUser_StoresHashedPasswordAndRequestedRole() {
        IServiceSecurity security = new clsServiceSecurity();
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(clsUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        clsServiceAdministrator service = new clsServiceAdministrator(repository, security);

        clsUser created = service.opCreateUser(validUserRequest());

        assertEquals(EnumRole.TEACHER, created.getAttRole());
        assertEquals("teacher@example.edu", created.getAttEmail());
        assertEquals(false, security.opValidatePassword("WrongPass1!", created.getAttPassword()));
        verify(repository).save(any(clsUser.class));
    }

    /** TEST-01-02: returns the persisted username so callers can identify the account. */
    @Test
    void opCreateUser_ReturnsPersistedUser() {
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(clsUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        clsServiceAdministrator service = new clsServiceAdministrator(repository, new clsServiceSecurity());

        clsUser created = service.opCreateUser(validUserRequest());

        assertEquals("teacher", created.getAttUsername());
    }

    /** TEST-01-03: rejects duplicate usernames before saving a second account. */
    @Test
    void opCreateUser_RejectsDuplicateUsername() {
        IRepositoryUser repository = mock(IRepositoryUser.class);
        when(repository.existsById("teacher")).thenReturn(true);
        clsServiceAdministrator service = new clsServiceAdministrator(repository, new clsServiceSecurity());

        assertThrows(IllegalArgumentException.class, () -> service.opCreateUser(validUserRequest()));
    }

    /** TEST-01-03: rejects invalid registration details without persisting a user. */
    @Test
    void opCreateUser_RejectsInvalidEmailWithoutSaving() {
        IRepositoryUser repository = mock(IRepositoryUser.class);
        clsServiceAdministrator service = new clsServiceAdministrator(repository, new clsServiceSecurity());
        rqtUser request = new rqtUser("teacher", "StrongPass1!", "Teacher Name", "invalid-email",
            EnumRole.TEACHER);

        assertThrows(IllegalArgumentException.class, () -> service.opCreateUser(request));

        verify(repository, never()).save(any(clsUser.class));
    }

    /** TEST-03-01: changes a user's role to one of the fixed UML values. */
    @Test
    void opModifyRole_StoresRoleFromFixedCatalog() {
        IRepositoryUser repository = mock(IRepositoryUser.class);
        clsUser existingUser = new clsUser("teacher", "hash", "Teacher", "teacher@example.edu",
            EnumRole.TEACHER);
        when(repository.opFindByUsername("teacher")).thenReturn(existingUser);
        when(repository.save(existingUser)).thenReturn(existingUser);
        clsServiceAdministrator service = new clsServiceAdministrator(repository, new clsServiceSecurity());

        service.opModifyRole("teacher", EnumRole.REVIEWER);

        assertEquals(EnumRole.REVIEWER, existingUser.getAttRole());
        verify(repository).save(existingUser);
    }

    /** Verifies update of a user's public fields and replacement password hash. */
    @Test
    void opUpdateUser_UpdatesStoredUser() {
        IServiceSecurity security = new clsServiceSecurity();
        IRepositoryUser repository = mock(IRepositoryUser.class);
        clsUser existingUser = new clsUser("teacher", "old-hash", "Old Name", "teacher@example.edu",
            EnumRole.TEACHER);
        when(repository.opFindByUsername("teacher")).thenReturn(existingUser);
        when(repository.findAll()).thenReturn(List.of(existingUser));
        when(repository.save(existingUser)).thenReturn(existingUser);
        clsServiceAdministrator service = new clsServiceAdministrator(repository, security);
        rqtUser request = new rqtUser("teacher", "NewStrongPass2!", "New Name",
            "new-teacher@example.edu", EnumRole.REVIEWER);

        clsUser updated = service.opUpdateUser(request);

        assertEquals("New Name", updated.getAttName());
        assertEquals("new-teacher@example.edu", updated.getAttEmail());
        assertEquals(EnumRole.REVIEWER, updated.getAttRole());
        assertEquals(true, security.opValidatePassword("NewStrongPass2!", updated.getAttPassword()));
    }

    /** Verifies that the administrator service returns all repository users. */
    @Test
    void opGetAllUsers_ReturnsPersistedUsers() {
        IRepositoryUser repository = mock(IRepositoryUser.class);
        List<clsUser> users = List.of(new clsUser("teacher", "hash", "Teacher",
            "teacher@example.edu", EnumRole.TEACHER));
        when(repository.findAll()).thenReturn(users);
        clsServiceAdministrator service = new clsServiceAdministrator(repository, new clsServiceSecurity());

        assertEquals(users, service.opGetAllUsers());
    }

    /** Builds a complete, valid administrator registration request. */
    private rqtUser validUserRequest() {
        return new rqtUser("teacher", "StrongPass1!", "Teacher Name", "teacher@example.edu",
            EnumRole.TEACHER);
    }
}