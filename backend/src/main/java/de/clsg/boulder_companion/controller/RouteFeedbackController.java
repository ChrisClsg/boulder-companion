package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.dto.feedback.RouteFeedbackDto;
import de.clsg.boulder_companion.dto.feedback.UpsertRouteFeedbackRequest;
import de.clsg.boulder_companion.service.CurrentUserService;
import de.clsg.boulder_companion.service.RouteFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes/{routeId}/my-feedback")
public class RouteFeedbackController {

    private final RouteFeedbackService routeFeedbackService;
    private final CurrentUserService currentUserService;

    public RouteFeedbackController(
            RouteFeedbackService routeFeedbackService,
            CurrentUserService currentUserService
    ) {
        this.routeFeedbackService = routeFeedbackService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<RouteFeedbackDto> getMyFeedback(@PathVariable String routeId) {
        String userId = currentUserService.getCurrentUserId();
        return routeFeedbackService.getMyFeedback(userId, routeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping
    public ResponseEntity<RouteFeedbackDto> upsertMyFeedback(
            @PathVariable String routeId,
            @RequestBody UpsertRouteFeedbackRequest request
    ) {
        String userId = currentUserService.getCurrentUserId();
        RouteFeedbackDto feedback = routeFeedbackService.upsertMyFeedback(userId, routeId, request);
        return ResponseEntity.ok(feedback);
    }
}
