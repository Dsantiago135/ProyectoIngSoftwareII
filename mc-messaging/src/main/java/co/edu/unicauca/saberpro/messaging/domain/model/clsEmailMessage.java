package co.edu.unicauca.saberpro.messaging.domain.model;

/** Mensaje de correo listo para enviar. */
public record clsEmailMessage(String to, String subject, String body) { }