package co.edu.unicauca.saberpro.question.domain.exception;

/** Excepción para invariantes incumplidas en el dominio de preguntas. */
public class clsDomainException extends RuntimeException {

    /** Crea un error de dominio con su explicación en español. */
    public clsDomainException(String prmMessage) {
        super(prmMessage);
    }
}