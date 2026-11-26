package co.edu.unicauca.saberpro.user.infrastructure.adapter.out.security;

import co.edu.unicauca.saberpro.user.domain.model.clsUser;
import co.edu.unicauca.saberpro.user.domain.ports.outbound.ITokenPort;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Adaptador para firmar JWT RS256 y publicar la llave pública como JWKS. */
@Component
public class clsJwtAdapter implements ITokenPort {
    private static final String KEY_ID = "saberpro-rs256-1";
    private final Path attPrivateKeyPath;
    private final Path attPublicKeyPath;
    private final String attIssuer;
    private final Duration attLifetime;

    public clsJwtAdapter(@Value("${security.jwt.private-key}") String prmPrivateKey,
                         @Value("${security.jwt.public-key}") String prmPublicKey,
                         @Value("${security.jwt.issuer}") String prmIssuer,
                         @Value("${security.jwt.lifetime:PT24H}") Duration prmLifetime) {
        attPrivateKeyPath = Path.of(prmPrivateKey);
        attPublicKeyPath = Path.of(prmPublicKey);
        attIssuer = prmIssuer;
        attLifetime = prmLifetime;
    }

    @Override
    public rpsToken opGenerate(clsUser prmUser) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(attLifetime);
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(attIssuer).subject(prmUser.getId().toString())
                .issueTime(Date.from(issuedAt)).expirationTime(Date.from(expiresAt))
                .claim("username", prmUser.getUsername()).claim("role", prmUser.getRole().name()).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KEY_ID).build(), claims);
        try {
            jwt.sign(new RSASSASigner(opReadPrivateKey()));
        } catch (JOSEException exception) {
            throw new IllegalStateException("No fue posible firmar el token", exception);
        }
        return new rpsToken(jwt.serialize(), expiresAt);
    }

    @Override
    public rpsJwks opGetPublicKeys() {
        RSAPublicKey publicKey = opReadPublicKey();
        String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(opUnsigned(publicKey.getModulus().toByteArray()));
        String exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(opUnsigned(publicKey.getPublicExponent().toByteArray()));
        return new rpsJwks(List.of(Map.of("kty", "RSA", "use", "sig", "alg", "RS256",
                "kid", KEY_ID, "n", modulus, "e", exponent)));
    }

    private RSAPrivateKey opReadPrivateKey() {
        try {
            byte[] encoded = opDecodePem(Files.readString(attPrivateKeyPath), "PRIVATE KEY");
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo leer la llave privada PKCS#8 configurada", exception);
        }
    }

    private RSAPublicKey opReadPublicKey() {
        try {
            byte[] encoded = opDecodePem(Files.readString(attPublicKeyPath), "PUBLIC KEY");
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo leer la llave pública X.509 configurada", exception);
        }
    }

    private byte[] opDecodePem(String prmPem, String prmLabel) {
        String content = prmPem.replace("-----BEGIN " + prmLabel + "-----", "")
                .replace("-----END " + prmLabel + "-----", "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(content);
    }

    private byte[] opUnsigned(byte[] prmValue) {
        return prmValue.length > 1 && prmValue[0] == 0 ? Arrays.copyOfRange(prmValue, 1, prmValue.length) : prmValue;
    }
}