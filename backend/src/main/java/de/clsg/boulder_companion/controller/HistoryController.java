package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.ClimbingHistoryService;
import de.clsg.boulder_companion.dto.ClimbingHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final ClimbingHistoryService climbingHistoryService;

    @Autowired
    public HistoryController(ClimbingHistoryService climbingHistoryService) {
        this.climbingHistoryService = climbingHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<ClimbingHistoryDto>> getHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String gymId,
            @RequestParam(required = false) String routeId) {
        if (userId != null) {
            return ResponseEntity.ok(climbingHistoryService.getHistoryByUserId(userId));
        }
        if (gymId != null) {
            return ResponseEntity.ok(climbingHistoryService.getHistoryByGymId(gymId));
        }
        if (routeId != null) {
            return ResponseEntity.ok(climbingHistoryService.getHistoryByRouteId(routeId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/topped")
    public ResponseEntity<List<ClimbingHistoryDto>> getToppedRoutes(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String gymId) {
        if (userId != null) {
            return ResponseEntity.ok(climbingHistoryService.getToppedRoutesByUserId(userId));
        }
        if (gymId != null) {
            return ResponseEntity.ok(climbingHistoryService.getHistoryByGymId(gymId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ClimbingHistoryDto>> getHistoryById(@PathVariable String id) {
        return ResponseEntity.ok(climbingHistoryService.getHistoryById(id));
    }

    @PostMapping
    public ResponseEntity<ClimbingHistoryDto> createHistory(@RequestBody ClimbingHistoryDto historyDto) {
        return ResponseEntity.ok(climbingHistoryService.createHistory(historyDto.toHistory()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClimbingHistoryDto> updateHistory(
            @PathVariable String id,
            @RequestBody ClimbingHistoryDto updatedHistoryDto) {
        return ResponseEntity.ok(climbingHistoryService.updateHistory(id, updatedHistoryDto.toHistory()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String id) {
        climbingHistoryService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
