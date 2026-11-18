package co.edu.unicauca.saberpro.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de MCAudit. */
@SpringBootApplication
public class AuditApplication {

    /** Inicia el microservicio de auditoría. */
    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }
}