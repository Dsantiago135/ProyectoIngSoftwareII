package co.edu.unicauca.saberpro.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Define los destinos internos del Gateway mediante configuración externa. */
@Configuration
public class GatewayRoutesConfiguration {

    /** Registra las rutas públicas hacia los microservicios de negocio. */
    @Bean
    RouteLocator opRoutes(RouteLocatorBuilder prmBuilder,
                          @Value("${services.mc-user}") String prmUserUri,
                          @Value("${services.mc-question}") String prmQuestionUri,
                          @Value("${services.mc-review}") String prmReviewUri,
                          @Value("${services.mc-audit}") String prmAuditUri) {
        return prmBuilder.routes()
                .route("mc-user", route -> route.path("/api/auth/**", "/api/users/**", "/.well-known/jwks.json").uri(prmUserUri))
                .route("mc-question", route -> route.path("/api/questions/**").uri(prmQuestionUri))
                .route("mc-review", route -> route.path("/api/reviews/**").uri(prmReviewUri))
                .route("mc-audit", route -> route.method("GET").and().path("/api/audit/**").uri(prmAuditUri))
                .build();
    }
}