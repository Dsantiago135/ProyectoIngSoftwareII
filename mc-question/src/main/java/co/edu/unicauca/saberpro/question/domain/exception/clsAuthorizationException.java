package co.edu.unicauca.saberpro.question.domain.exception;

/** Excepción para acciones realizadas por un actor sin autorización de dominio. */
public class clsAuthorizationException extends RuntimeException {
    public clsAuthorizationException(String prmMessage) { super(prmMessage); }
}