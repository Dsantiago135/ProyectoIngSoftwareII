package com.dsantiago.proyectolab.Application.pkgServices;

public interface IServiceSecurity {
    
    /**
     * Generates a secure hash for a plaintext password.
     * @param prmPassword plaintext password
     * @return encoded password hash
     */
    String opEncryptPassword(String prmPassword);

    /**
     * Checks a plaintext password against its stored hash.
     * @param prmPassword plaintext password
     * @param prmStoredHash encoded password hash
     * @return true when the password matches
     */
    boolean opValidatePassword(String prmPassword, String prmStoredHash);
}
