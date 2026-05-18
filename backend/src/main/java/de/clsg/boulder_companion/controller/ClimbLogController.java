package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.dto.climblog.ClimbLogDto;
import de.clsg.boulder_companion.dto.climblog.CreateClimbLogRequest;
import de.clsg.boulder_companion.dto.climblog.UpdateClimbLogRequest;
import de.clsg.boulder_companion.service.ClimbLogService;
import de.clsg.boulder_companion.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/climb-logs")
public class ClimbLogController {

    private final ClimbLogService climbLogService;
    private final CurrentUserService currentUserService;

    public ClimbLogController(
            ClimbLogService climbLogService,
            CurrentUserService currentUserService
    ) {
        this.climbLogService = climbLogService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<List<ClimbLogDto>> getClimbLogs(
            @RequestParam(required = false) String gymId,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) Boolean topped,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        String userId = currentUserService.getCurrentUserId();

        List<ClimbLogDto> logs = climbLogService.getClimbLogs(
                userId,
                gymId,
                routeId,
                sessionId,
                topped,
                from,
                to
        );

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClimbLogDto> getClimbLogById(
            @PathVariable String id
    ) {
        String userId = currentUserService.getCurrentUserId();

        return climbLogService.getClimbLogById(userId, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClimbLogDto> createClimbLog(
            @RequestBody CreateClimbLogRequest request
    ) {
        String userId = currentUserService.getCurrentUserId();

        ClimbLogDto createdLog = climbLogService.createClimbLog(userId, request);

        return ResponseEntity.ok(createdLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClimbLogDto> updateClimbLog(
            @PathVariable String id,
            @RequestBody UpdateClimbLogRequest request
    ) {
        String userId = currentUserService.getCurrentUserId();

        ClimbLogDto updatedLog = climbLogService.updateClimbLog(userId, id, request);

        return ResponseEntity.ok(updatedLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClimbLog(
            @PathVariable String id
    ) {
        String userId = currentUserService.getCurrentUserId();

        climbLogService.deleteClimbLog(userId, id);

        return ResponseEntity.noContent().build();
    }
}