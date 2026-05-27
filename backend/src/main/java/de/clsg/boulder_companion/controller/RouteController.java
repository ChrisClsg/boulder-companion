package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.RouteService;
import de.clsg.boulder_companion.dto.RouteDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<RouteDto>> getRoutesByGym(
            @RequestParam(required = false) String gymId) {
        if (gymId != null) {
            return ResponseEntity.ok(routeService.getRoutesByGymId(gymId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable String id) {
        return routeService.getRouteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
