package co.edu.unicauca.saberpro.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de MCUser. */
@SpringBootApplication
public class UserApplication {

    /** Inicia el microservicio de usuarios. */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}