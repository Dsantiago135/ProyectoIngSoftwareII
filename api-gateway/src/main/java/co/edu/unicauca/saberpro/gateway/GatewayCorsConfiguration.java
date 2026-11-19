package co.edu.unicauca.saberpro.gateway;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/** Permite llamadas desde el origen local configurado para la interfaz web. */
@Configuration
public class GatewayCorsConfiguration {

    /** Registra CORS para el servidor estático de la UI y limita verbos y cabeceras. */
    @Bean
    CorsWebFilter opCorsWebFilter(@Value("${ui.allowed-origin:http://localhost:5500}") String prmAllowedOrigin) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(prmAllowedOrigin));
        cors.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(), HttpMethod.OPTIONS.name()));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return new CorsWebFilter(source);
    }
}