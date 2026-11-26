package co.edu.unicauca.saberpro.audit.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.audit.domain.exception.clsAuditException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(clsAuditException.class)
    public ResponseEntity<Map<String, String>> opHandleAudit(clsAuditException prmException) {
        return ResponseEntity.status(prmException.getStatus()).body(Map.of("mensaje", prmException.getMessage()));
    }
}