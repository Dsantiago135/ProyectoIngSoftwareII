package co.edu.unicauca.saberpro.review.infrastructure.adapter.in.web;

import co.edu.unicauca.saberpro.review.domain.model.clsReview;
import co.edu.unicauca.saberpro.review.domain.model.enmDecision;
import co.edu.unicauca.saberpro.review.domain.ports.inbound.IReviewUseCase;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {
    private final IReviewUseCase attReviewUseCase;
    public ReviewRestController(IReviewUseCase prmReviewUseCase) { attReviewUseCase = prmReviewUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public rpsReview opCreate(@RequestBody rqtReview prmRequest, @RequestHeader("X-User-Id") UUID prmReviewerId) {
        return opToResponse(attReviewUseCase.opCreate(
                new IReviewUseCase.rqtReview(prmRequest.questionId(), prmRequest.decision(), prmRequest.justification()),
                prmReviewerId));
    }

    @GetMapping("/{id}")
    public rpsReview opFindById(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID prmActorId,
                                @RequestHeader("X-User-Role") String prmActorRole) {
        return opToResponse(attReviewUseCase.opFindById(id, prmActorId, prmActorRole));
    }

    @GetMapping
    public List<rpsReview> opFindByQuestionId(@RequestParam UUID questionId,
                                              @RequestHeader("X-User-Id") UUID prmActorId,
                                              @RequestHeader("X-User-Role") String prmActorRole) {
        return attReviewUseCase.opFindByQuestionId(questionId, prmActorId, prmActorRole)
                .stream().map(this::opToResponse).toList();
    }

    private rpsReview opToResponse(clsReview prmReview) {
        return new rpsReview(prmReview.id(), prmReview.questionId(), prmReview.reviewerId(),
                prmReview.decision(), prmReview.justification(), prmReview.createdAt());
    }

    public record rqtReview(UUID questionId, enmDecision decision, String justification) { }
    public record rpsReview(UUID id, UUID questionId, UUID reviewerId, enmDecision decision,
                            String justification, Instant createdAt) { }
}