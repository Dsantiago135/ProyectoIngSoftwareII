package co.edu.unicauca.saberpro.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de MCMessaging. */
@SpringBootApplication
public class MessagingApplication {

    /** Inicia el microservicio de notificaciones. */
    public static void main(String[] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }
}