package com.dsantiago.proyectolab.Application.pkgServices;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class clsServiceSecurity implements IServiceSecurity {

    /**
     * Creates a salted PBKDF2 hash that can be stored for later verification.
     * @param prmPassword plaintext password
     * @return encoded hash containing algorithm parameters, salt, and digest
     */
    @Override
    public String opEncryptPassword(String prmPassword) {
        if (prmPassword == null) throw new IllegalArgumentException("Password must not be null");

        byte[] varSalt = new byte[16];
        new SecureRandom().nextBytes(varSalt);
        PBEKeySpec varKeySpec = new PBEKeySpec(prmPassword.toCharArray(), varSalt, 210_000, 256);
        try {
            byte[] varHash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(varKeySpec).getEncoded();
            return "pbkdf2-sha256$210000$"
                + Base64.getEncoder().withoutPadding().encodeToString(varSalt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(varHash);
        } catch (GeneralSecurityException prmException) {
            log.error("Failed to hash password.", prmException);
            throw new IllegalStateException("Failed to hash password", prmException);
        } finally {
            varKeySpec.clearPassword();
        }
    }

    /**
     * Verifies a plaintext password against the encoded stored hash.
     * @param prmPassword plaintext password
     * @param prmStoredHash encoded password hash
     * @return true when both passwords match
     */
    @Override
    public boolean opValidatePassword(String prmPassword, String prmStoredHash) {
        if (prmPassword == null || prmStoredHash == null) {
            return false;
        }
        try {
            String[] varParts = prmStoredHash.split("\\$", -1);
            if (varParts.length != 4 || !"pbkdf2-sha256".equals(varParts[0])) {
                return false;
            }
            int varIterations = Integer.parseInt(varParts[1]);
            byte[] varSalt = Base64.getDecoder().decode(varParts[2]);
            byte[] varExpectedHash = Base64.getDecoder().decode(varParts[3]);
            if (varIterations < 210_000 || varIterations > 1_000_000
                || varSalt.length != 16 || varExpectedHash.length != 32) {
                return false;
            }
            PBEKeySpec varKeySpec = new PBEKeySpec(prmPassword.toCharArray(), varSalt,
                varIterations, varExpectedHash.length * 8);
            byte[] varActualHash;
            try {
                varActualHash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(varKeySpec).getEncoded();
            } finally {
                varKeySpec.clearPassword();
            }
            return MessageDigest.isEqual(varExpectedHash, varActualHash);
        } catch (GeneralSecurityException | IllegalArgumentException prmException) {
            log.warn("Stored password hash could not be verified.");
            return false;
        }
    }

}