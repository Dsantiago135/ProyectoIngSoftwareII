package co.edu.unicauca.saberpro.user.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.user.domain.exception.clsUserException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(clsUserException.class)
    public ResponseEntity<Map<String, String>> opHandleUser(clsUserException prmException) {
        return ResponseEntity.status(prmException.getStatus()).body(Map.of("mensaje", prmException.getMessage()));
    }
}