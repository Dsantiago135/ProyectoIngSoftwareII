package co.edu.unicauca.saberpro.user.domain.ports.inbound;

import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsJwks;

/** Puerto de entrada para publicar claves públicas de validación JWT. */
public interface IPublicKeyUseCase {
    rpsJwks opGetPublicKeys();
}