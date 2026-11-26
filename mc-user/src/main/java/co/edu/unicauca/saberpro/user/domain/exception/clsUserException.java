package co.edu.unicauca.saberpro.user.domain.exception;

public class clsUserException extends RuntimeException {
    private final int attStatus;
    public clsUserException(String prmMessage) { this(400, prmMessage); }
    public clsUserException(int prmStatus, String prmMessage) { super(prmMessage); attStatus = prmStatus; }
    public int getStatus() { return attStatus; }
}