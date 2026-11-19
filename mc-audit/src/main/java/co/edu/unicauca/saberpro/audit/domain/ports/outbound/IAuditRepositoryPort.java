package co.edu.unicauca.saberpro.audit.domain.ports.outbound;

import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import java.util.Optional;
import java.util.UUID;

/** Puerto de auditoría para insertar y consultar, sin actualización ni borrado. */
public interface IAuditRepositoryPort {
    clsAuditRecord opSave(clsAuditRecord prmRecord);
    boolean opExistsByEventId(UUID prmEventId);
    Optional<clsAuditRecord> opFindById(UUID prmId);
    rpsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize);
}