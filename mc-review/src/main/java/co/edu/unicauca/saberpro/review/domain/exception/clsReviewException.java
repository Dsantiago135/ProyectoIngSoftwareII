package co.edu.unicauca.saberpro.review.domain.exception;

/** Excepción de negocio de MCReview con su código HTTP previsto. */
public class clsReviewException extends RuntimeException {
    private final int attStatus;
    public clsReviewException(int prmStatus, String prmMessage) { super(prmMessage); attStatus = prmStatus; }
    public int getStatus() { return attStatus; }
}