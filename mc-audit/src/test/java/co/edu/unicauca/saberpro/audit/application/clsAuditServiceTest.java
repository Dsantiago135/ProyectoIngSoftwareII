package co.edu.unicauca.saberpro.audit.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unicauca.saberpro.audit.application.service.clsAuditService;
import co.edu.unicauca.saberpro.audit.domain.exception.clsAuditException;
import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditEvent;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import co.edu.unicauca.saberpro.audit.domain.ports.outbound.IAuditRepositoryPort;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class clsAuditServiceTest {
    @Test
    void recordsEventOnlyOnceForDuplicateEventId() {
        InMemoryAuditRepository repository = new InMemoryAuditRepository();
        clsAuditService service = new clsAuditService(repository, new ObjectMapper());
        rqtAuditEvent event = new rqtAuditEvent(UUID.randomUUID(), "question.created", Instant.now(),
                "MCQuestion", UUID.randomUUID(), "QUESTION", UUID.randomUUID(), java.util.Map.of("status", "BORRADOR"));

        service.opRecord(event);
        service.opRecord(event);

        assertEquals(1, repository.records.size());
    }

    @Test
    void rejectsPageSizesAboveLimit() {
        clsAuditService service = new clsAuditService(new InMemoryAuditRepository(), new ObjectMapper());

        clsAuditException exception = assertThrows(clsAuditException.class,
                () -> service.opSearch(new rqtAuditFilter(null, null, null, null, null), 0, 101));

        assertEquals(400, exception.getStatus());
    }

    private static final class InMemoryAuditRepository implements IAuditRepositoryPort {
        private final List<clsAuditRecord> records = new ArrayList<>();
        @Override public clsAuditRecord opSave(clsAuditRecord prmRecord) { records.add(prmRecord); return prmRecord; }
        @Override public boolean opExistsByEventId(UUID prmEventId) {
            return records.stream().anyMatch(record -> record.eventId().equals(prmEventId));
        }
        @Override public Optional<clsAuditRecord> opFindById(UUID prmId) {
            return records.stream().filter(record -> record.id().equals(prmId)).findFirst();
        }
        @Override public rpsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize) {
            return new rpsPage<>(records, prmPage, prmSize, records.size());
        }
    }
}