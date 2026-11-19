package co.edu.unicauca.saberpro.messaging.domain.ports.outbound;

import java.util.UUID;

/** Puerto para resolver el correo de un usuario desde MCUser. */
public interface IRecipientLookupPort {
    String opFindEmail(UUID prmUserId);
}