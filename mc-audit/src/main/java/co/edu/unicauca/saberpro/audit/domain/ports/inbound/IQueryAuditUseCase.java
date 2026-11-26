package co.edu.unicauca.saberpro.audit.domain.ports.inbound;

import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import java.util.UUID;

/** Puerto de entrada para consultas de solo lectura de auditoría. */
public interface IQueryAuditUseCase {
    clsAuditRecord opFindById(UUID prmId);
    rpsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize);
}