package co.edu.unicauca.saberpro.audit.application.service;

import co.edu.unicauca.saberpro.audit.domain.exception.clsAuditException;
import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditEvent;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IQueryAuditUseCase;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IRecordAuditUseCase;
import co.edu.unicauca.saberpro.audit.domain.ports.outbound.IAuditRepositoryPort;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;

public class clsAuditService implements IRecordAuditUseCase, IQueryAuditUseCase {
    private final IAuditRepositoryPort attRepository;
    private final ObjectMapper attMapper;

    public clsAuditService(IAuditRepositoryPort prmRepository, ObjectMapper prmMapper) {
        attRepository = prmRepository;
        attMapper = prmMapper;
    }

    @Override
    public void opRecord(rqtAuditEvent prmEvent) {
        if (prmEvent == null) throw new clsAuditException(400, "El evento de auditoría es obligatorio");
        if (attRepository.opExistsByEventId(prmEvent.eventId())) return;
        String detail;
        try {
            detail = prmEvent.detail() == null ? "{}" : attMapper.writeValueAsString(prmEvent.detail());
        } catch (RuntimeException exception) {
            throw new clsAuditException(400, "El detalle del evento no se puede serializar");
        }
        try {
            attRepository.opSave(clsAuditRecord.opCreate(prmEvent.eventId(), prmEvent.actorId(),
                    prmEvent.eventType(), prmEvent.entityType(), prmEvent.entityId(), detail,
                    prmEvent.sourceService(), prmEvent.occurredAt()));
        } catch (IllegalArgumentException exception) {
            throw new clsAuditException(400, exception.getMessage());
        } catch (RuntimeException exception) {
            if (attRepository.opExistsByEventId(prmEvent.eventId())) return;
            throw exception;
        }
    }

    @Override
    public clsAuditRecord opFindById(UUID prmId) {
        return attRepository.opFindById(prmId)
                .orElseThrow(() -> new clsAuditException(404, "No existe el registro de auditoría"));
    }

    @Override
    public rpsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize) {
        if (prmPage < 0 || prmSize < 1 || prmSize > 100) {
            throw new clsAuditException(400, "La página debe ser positiva y el tamaño debe estar entre 1 y 100");
        }
        if (prmFilter != null && prmFilter.from() != null && prmFilter.to() != null
                && prmFilter.from().isAfter(prmFilter.to())) {
            throw new clsAuditException(400, "La fecha inicial no puede ser posterior a la fecha final");
        }
        return attRepository.opSearch(prmFilter == null
                ? new rqtAuditFilter(null, null, null, null, null) : prmFilter, prmPage, prmSize);
    }
}