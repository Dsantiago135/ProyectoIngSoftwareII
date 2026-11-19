package co.edu.unicauca.saberpro.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del API Gateway reactivo. */
@SpringBootApplication
public class GatewayApplication {

    /** Inicia el Gateway. */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}