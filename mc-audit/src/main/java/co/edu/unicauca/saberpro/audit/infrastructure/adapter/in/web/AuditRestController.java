package co.edu.unicauca.saberpro.audit.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import co.edu.unicauca.saberpro.audit.domain.ports.inbound.IQueryAuditUseCase;
import java.time.Instant;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditRestController {
    private final IQueryAuditUseCase attQueryUseCase;
    public AuditRestController(IQueryAuditUseCase prmQueryUseCase) { attQueryUseCase = prmQueryUseCase; }

    @GetMapping
    public rpsPage<clsAuditRecord> opSearch(@RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String entityType, @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return attQueryUseCase.opSearch(new rqtAuditFilter(actorId, entityType, entityId, from, to), page, size);
    }

    @GetMapping("/{id}")
    public clsAuditRecord opFindById(@PathVariable UUID id) { return attQueryUseCase.opFindById(id); }
}