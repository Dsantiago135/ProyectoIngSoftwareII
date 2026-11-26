package co.edu.unicauca.saberpro.review.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.review.domain.exception.clsReviewException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(clsReviewException.class)
    public ResponseEntity<Map<String, String>> opHandleReview(clsReviewException prmException) {
        return ResponseEntity.status(prmException.getStatus()).body(Map.of("mensaje", prmException.getMessage()));
    }
}