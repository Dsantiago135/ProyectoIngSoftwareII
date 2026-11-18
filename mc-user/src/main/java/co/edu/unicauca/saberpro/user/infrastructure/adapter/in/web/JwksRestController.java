package co.edu.unicauca.saberpro.user.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.user.domain.ports.inbound.IPublicKeyUseCase;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort.rpsJwks;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Publica la llave RSA pública usada por el Gateway para validar tokens. */
@RestController
public class JwksRestController {
    private final IPublicKeyUseCase attPublicKeyUseCase;
    public JwksRestController(IPublicKeyUseCase prmPublicKeyUseCase) { attPublicKeyUseCase = prmPublicKeyUseCase; }

    /** Devuelve el conjunto de llaves en formato JWKS. */
    @GetMapping("/.well-known/jwks.json")
    public rpsJwks opGetPublicKeys() { return attPublicKeyUseCase.opGetPublicKeys(); }
}