package co.edu.unicauca.saberpro.audit.domain.exception;

/** Excepción de validación y consulta en MCAudit. */
public class clsAuditException extends RuntimeException {
    private final int attStatus;
    public clsAuditException(int prmStatus, String prmMessage) { super(prmMessage); attStatus = prmStatus; }
    public int getStatus() { return attStatus; }
}