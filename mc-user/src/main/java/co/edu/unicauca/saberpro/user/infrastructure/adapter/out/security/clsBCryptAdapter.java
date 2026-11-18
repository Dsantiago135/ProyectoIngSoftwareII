package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.security;

import co.edu.unicauca.saberpro.user.domain.ports.outbound.IPasswordHashPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Adaptador BCrypt para guardar y validar contraseñas. */
@Component
public class clsBCryptAdapter implements IPasswordHashPort {
    private final BCryptPasswordEncoder attEncoder = new BCryptPasswordEncoder();
    @Override public String opHash(String prmPassword) { return attEncoder.encode(prmPassword); }
    @Override public boolean opMatches(String prmPassword, String prmHash) { return attEncoder.matches(prmPassword, prmHash); }
}