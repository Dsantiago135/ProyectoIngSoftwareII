package co.edu.unicauca.saberpro.question.domain.exception;

/** Excepción para una pregunta que no existe en el agregado consultado. */
public class clsQuestionNotFoundException extends RuntimeException {
    public clsQuestionNotFoundException(String prmMessage) { super(prmMessage); }
}