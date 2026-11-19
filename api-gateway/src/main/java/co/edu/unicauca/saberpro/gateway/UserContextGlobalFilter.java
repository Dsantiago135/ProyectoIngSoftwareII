package co.edu.unicauca.saberpro.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Propaga identidad confiable del token y descarta cabeceras enviadas por el cliente. */
@Component
public class UserContextGlobalFilter implements GlobalFilter, Ordered {

    /** Limpia las cabeceras de identidad y las completa desde el JWT validado. */
    @Override
    public Mono<Void> filter(ServerWebExchange prmExchange, GatewayFilterChain prmChain) {
        ServerWebExchange sanitizedExchange = prmExchange.mutate()
                .request(request -> request.headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Role");
                }))
                .build();

        return sanitizedExchange.getPrincipal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> sanitizedExchange.mutate()
                        .request(request -> request.headers(headers -> {
                            headers.set("X-User-Id", authentication.getToken().getSubject());
                            String role = authentication.getToken().getClaimAsString("role");
                            if (role != null) {
                                headers.set("X-User-Role", role);
                            }
                        }))
                        .build())
                .defaultIfEmpty(sanitizedExchange)
                .flatMap(prmChain::filter);
    }

    /** Ejecuta el filtro antes de que la solicitud salga hacia el microservicio. */
    @Override
    public int getOrder() {
        return -1;
    }
}