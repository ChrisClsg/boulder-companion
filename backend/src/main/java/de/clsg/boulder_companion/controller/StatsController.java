package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.dto.stats.ClimbStatsSummaryDto;
import de.clsg.boulder_companion.service.CurrentUserService;
import de.clsg.boulder_companion.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/stats")
public class StatsController {

    private final StatsService statsService;
    private final CurrentUserService currentUserService;

    public StatsController(
            StatsService statsService,
            CurrentUserService currentUserService
    ) {
        this.statsService = statsService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ClimbStatsSummaryDto> getSummary() {
        String userId = currentUserService.getCurrentUserId();
        ClimbStatsSummaryDto summary = statsService.getSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
