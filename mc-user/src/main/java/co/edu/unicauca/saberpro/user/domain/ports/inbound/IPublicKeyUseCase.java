package co.edu.unicauca.saberpro.user.domain.ports.inbound;

import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsJwks;


public interface IPublicKeyUseCase {
    rpsJwks opGetPublicKeys();
}