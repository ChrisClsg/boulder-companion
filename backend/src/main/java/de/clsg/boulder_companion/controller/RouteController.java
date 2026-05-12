package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.RouteService;
import de.clsg.boulder_companion.dto.RouteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<RouteDto>> getRoutesByGym(
            @RequestParam(required = false) String gymId,
            @AuthenticationPrincipal OAuth2User user) {
        if (gymId != null) {
            return ResponseEntity.ok(routeService.getRoutesByGymId(gymId));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<RouteDto>> getRouteById(@PathVariable String id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/{gymId}/archived")
    public ResponseEntity<List<RouteDto>> getArchivedRoutes(
            @PathVariable String gymId,
            @AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok(routeService.getRoutesByGymIdAndArchived(gymId, true));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROUTE_SETTER')")
    public ResponseEntity<RouteDto> createRoute(@RequestBody RouteDto routeDto) {
        Route route = routeDto.toRoute();
        return ResponseEntity.ok(routeService.createRoute(route));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROUTE_SETTER')")
    public ResponseEntity<RouteDto> updateRoute(
            @PathVariable String id,
            @RequestBody RouteDto updatedRouteDto) {
        return ResponseEntity.ok(routeService.updateRoute(id, updatedRouteDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable String id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> archiveRoute(@PathVariable String id) {
        return ResponseEntity.ok(routeService.archiveRoute(id));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROUTE_SETTER')")
    public ResponseEntity<List<RouteDto>> bulkCreateRoutes(@RequestBody BulkCreateRequest request) {
        return ResponseEntity.ok(request.routes().stream()
            .map(routeService::createRoute)
            .toList());
    }

    record BulkCreateRequest(String gymId, List<RouteDto> routes) {}
}
