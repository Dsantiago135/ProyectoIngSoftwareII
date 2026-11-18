package co.edu.unicauca.saberpro.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/** Reglas de autenticación y autorización centralizadas para las rutas públicas. */
@Configuration
public class GatewaySecurityConfiguration {

    /** Configura acceso público limitado y autorización basada en el rol del JWT. */
    @Bean
    SecurityWebFilterChain opSecurityFilterChain(ServerHttpSecurity prmHttp) {
        return prmHttp.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/api/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/audit/**").hasRole("ADMIN")
                        .pathMatchers("/api/audit/**").denyAll()
                        .pathMatchers(HttpMethod.PATCH, "/api/questions/*/status").denyAll()
                        .pathMatchers(HttpMethod.POST, "/api/reviews").hasRole("REVIEWER")
                        .pathMatchers(HttpMethod.POST, "/api/questions/*/publish", "/api/questions/*/archive").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/questions", "/api/questions/*", "/api/questions/*/submit", "/api/questions/*/reopen").hasRole("AUTHOR")
                        .pathMatchers("/api/questions/**").hasAnyRole("AUTHOR", "REVIEWER", "ADMIN", "TEACHER")
                        .pathMatchers("/api/reviews/**").hasAnyRole("REVIEWER", "ADMIN", "AUTHOR")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(opJwtAuthenticationConverter())))
                .build();
    }

    /** Convierte el claim role del token en la autoridad que usa Spring Security. */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> opJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(authenticationConverter);
    }
}