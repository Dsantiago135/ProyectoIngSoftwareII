package co.edu.unicauca.saberpro.audit.infrastructure.adapter.out.persistence.jpa;

import co.edu.unicauca.saberpro.audit.domain.model.clsAuditRecord;
import co.edu.unicauca.saberpro.audit.domain.model.rqtAuditFilter;
import co.edu.unicauca.saberpro.audit.domain.model.rpsPage;
import co.edu.unicauca.saberpro.audit.domain.ports.outbound.IAuditRepositoryPort;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class clsAuditJpaAdapter implements IAuditRepositoryPort {
    private final SpringDataAuditRepository attRepository;
    public clsAuditJpaAdapter(SpringDataAuditRepository prmRepository) { attRepository = prmRepository; }

    @Override
    public clsAuditRecord opSave(clsAuditRecord prmRecord) {
        clsAuditEntity entity = new clsAuditEntity(prmRecord.id(), prmRecord.eventId(), prmRecord.actorId(),
                prmRecord.action(), prmRecord.entityType(), prmRecord.entityId(), prmRecord.detail(),
                prmRecord.sourceService(), prmRecord.occurredAt(), prmRecord.recordedAt());
        return opToDomain(attRepository.save(entity));
    }

    @Override public boolean opExistsByEventId(UUID prmEventId) { return attRepository.existsByEventId(prmEventId); }
    @Override public Optional<clsAuditRecord> opFindById(UUID prmId) { return attRepository.findById(prmId).map(this::opToDomain); }

    @Override
    public rpsPage<clsAuditRecord> opSearch(rqtAuditFilter prmFilter, int prmPage, int prmSize) {
        Specification<clsAuditEntity> specification = (root, query, builder) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            if (prmFilter.actorId() != null) predicates.add(builder.equal(root.get("actorId"), prmFilter.actorId()));
            if (prmFilter.entityType() != null && !prmFilter.entityType().isBlank()) {
                predicates.add(builder.equal(builder.upper(root.get("entityType")), prmFilter.entityType().toUpperCase()));
            }
            if (prmFilter.entityId() != null) predicates.add(builder.equal(root.get("entityId"), prmFilter.entityId()));
            if (prmFilter.from() != null) predicates.add(builder.greaterThanOrEqualTo(root.get("occurredAt"), prmFilter.from()));
            if (prmFilter.to() != null) predicates.add(builder.lessThanOrEqualTo(root.get("occurredAt"), prmFilter.to()));
            return builder.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        Page<clsAuditEntity> result = attRepository.findAll(specification,
                PageRequest.of(prmPage, prmSize, Sort.by(Sort.Direction.DESC, "occurredAt")));
        return new rpsPage<>(result.getContent().stream().map(this::opToDomain).toList(), prmPage, prmSize,
                result.getTotalElements());
    }

    private clsAuditRecord opToDomain(clsAuditEntity prmEntity) {
        return new clsAuditRecord(prmEntity.getId(), prmEntity.getEventId(), prmEntity.getActorId(),
                prmEntity.getAction(), prmEntity.getEntityType(), prmEntity.getEntityId(), prmEntity.getDetail(),
                prmEntity.getSourceService(), prmEntity.getOccurredAt(), prmEntity.getRecordedAt());
    }
}