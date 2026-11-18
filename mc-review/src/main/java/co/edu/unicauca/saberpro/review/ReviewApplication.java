package co.edu.unicauca.saberpro.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de MCReview. */
@SpringBootApplication
public class ReviewApplication {

    /** Inicia el microservicio de revisiones. */
    public static void main(String[] args) {
        SpringApplication.run(ReviewApplication.class, args);
    }
}