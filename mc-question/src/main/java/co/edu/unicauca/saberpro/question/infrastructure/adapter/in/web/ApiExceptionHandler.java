package co.edu.unicauca.saberpro.question.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.question.domain.exception.clsAuthorizationException;
import co.edu.unicauca.saberpro.question.domain.exception.clsConflictException;
import co.edu.unicauca.saberpro.question.domain.exception.clsDomainException;
import co.edu.unicauca.saberpro.question.domain.exception.clsQuestionNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(clsDomainException.class)
    public ResponseEntity<Map<String, String>> opHandleDomain(clsDomainException prmException) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", prmException.getMessage()));
    }

    @ExceptionHandler(clsAuthorizationException.class)
    public ResponseEntity<Map<String, String>> opHandleAuthorization(clsAuthorizationException prmException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", prmException.getMessage()));
    }

    @ExceptionHandler(clsConflictException.class)
    public ResponseEntity<Map<String, String>> opHandleConflict(clsConflictException prmException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", prmException.getMessage()));
    }

    @ExceptionHandler(clsQuestionNotFoundException.class)
    public ResponseEntity<Map<String, String>> opHandleNotFound(clsQuestionNotFoundException prmException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", prmException.getMessage()));
    }
}