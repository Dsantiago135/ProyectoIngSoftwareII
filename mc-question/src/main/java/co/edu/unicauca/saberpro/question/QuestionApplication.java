package co.edu.unicauca.saberpro.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de MCQuestion. */
@SpringBootApplication
public class QuestionApplication {

    /** Inicia el microservicio de preguntas. */
    public static void main(String[] args) {
        SpringApplication.run(QuestionApplication.class, args);
    }
}