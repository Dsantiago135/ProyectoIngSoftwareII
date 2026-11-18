package co.edu.unicauca.saberpro.user.domain.ports.outbound;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Puerto de salida para emitir JWT y consultar el documento JWKS. */
public interface ITokenPort {
    record rpsToken(String token, Instant expiresAt) { }
    record rpsJwks(List<Map<String, Object>> keys) { }
    rpsToken opGenerate(clsUser prmUser);
    rpsJwks opGetPublicKeys();
}