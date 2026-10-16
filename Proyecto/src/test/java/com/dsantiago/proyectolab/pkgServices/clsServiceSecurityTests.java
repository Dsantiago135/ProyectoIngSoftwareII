package com.dsantiago.proyectolab.pkgServices;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dsantiago.proyectolab.Application.pkgServices.clsServiceSecurity;

class clsServiceSecurityTests {

    /** Verifies that equal passwords receive distinct salted hashes. */
    @Test
    void opEncryptPassword_UsesSaltedHash() {
        clsServiceSecurity security = new clsServiceSecurity();

        String firstHash = security.opEncryptPassword("StrongPass1!");
        String secondHash = security.opEncryptPassword("StrongPass1!");

        assertNotEquals("StrongPass1!", firstHash);
        assertNotEquals(firstHash, secondHash);
    }

    /** Verifies valid, invalid, and malformed password hashes. */
    @Test
    void opValidatePassword_RejectsInvalidCredentialsAndHash() {
        clsServiceSecurity security = new clsServiceSecurity();
        String storedHash = security.opEncryptPassword("StrongPass1!");

        assertTrue(security.opValidatePassword("StrongPass1!", storedHash));
        assertFalse(security.opValidatePassword("WrongPass1!", storedHash));
        assertFalse(security.opValidatePassword("StrongPass1!", "not-a-hash"));
    }
}