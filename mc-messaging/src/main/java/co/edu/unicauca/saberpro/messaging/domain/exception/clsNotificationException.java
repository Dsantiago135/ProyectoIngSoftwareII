package co.edu.unicauca.saberpro.messaging.domain.exception;

/** Excepción recuperable de procesamiento de notificaciones. */
public class clsNotificationException extends RuntimeException {
    public clsNotificationException(String prmMessage) { super(prmMessage); }
    public clsNotificationException(String prmMessage, Throwable prmCause) { super(prmMessage, prmCause); }
}