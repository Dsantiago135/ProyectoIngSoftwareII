package co.edu.unicauca.saberpro.question.domain.exception;

/** Excepción para transiciones incompatibles con el estado actual. */
public class clsConflictException extends RuntimeException {
    public clsConflictException(String prmMessage) { super(prmMessage); }
}