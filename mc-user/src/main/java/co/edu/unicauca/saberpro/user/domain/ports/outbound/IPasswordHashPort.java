package co.edu.unicauca.saberpro.user.domain.ports.outbound;

/** Puerto de salida para hash y comparación de contraseñas. */
public interface IPasswordHashPort {
    String opHash(String prmPassword);
    boolean opMatches(String prmPassword, String prmHash);
}