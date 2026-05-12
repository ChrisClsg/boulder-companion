package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.repository.RouteRepository;
import de.clsg.boulder_companion.dto.RouteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    @Autowired
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional(readOnly = true)
    public List<RouteDto> getRoutesByGymId(String gymId) {
        return routeRepository.findByGymId(gymId).stream()
            .map(RouteDto::fromRoute)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RouteDto> getRouteById(String id) {
        return routeRepository.findById(id).map(RouteDto::fromRoute);
    }

    @Transactional(readOnly = true)
    public List<RouteDto> getRoutesByGymIdAndArchived(String gymId, boolean archived) {
        return routeRepository.findByGymIdAndArchived(gymId, archived).stream()
            .map(RouteDto::fromRoute)
            .toList();
    }

    @Transactional
    public RouteDto createRoute(Route route) {
        return RouteDto.fromRoute(routeRepository.save(route));
    }

    @Transactional
    public RouteDto updateRoute(String id, Route updatedRoute) {
        Route existingRoute = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        existingRoute = new Route(
            existingRoute.id(),
            existingRoute.gymId(),
            updatedRoute.name(),
            updatedRoute.difficulty(),
            updatedRoute.holdColor(),
            updatedRoute.holdTypes() != null ? updatedRoute.holdTypes() : existingRoute.holdTypes(),
            existingRoute.setterId(),
            updatedRoute.wall(),
            updatedRoute.archived(),
            updatedRoute.archived() ? Instant.now() : existingRoute.archivedAt(),
            updatedRoute.images() != null ? updatedRoute.images() : existingRoute.images(),
            existingRoute.createdAt(),
            System.currentTimeMillis()
        );

        return RouteDto.fromRoute(routeRepository.save(existingRoute));
    }

    @Transactional
    public void deleteRoute(String id) {
        routeRepository.deleteById(id);
    }

    @Transactional
    public RouteDto archiveRoute(String id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        route = new Route(
            route.id(),
            route.gymId(),
            route.name(),
            route.difficulty(),
            route.holdColor(),
            route.holdTypes(),
            route.setterId(),
            route.wall(),
            true,
            Instant.now(),
            route.images(),
            route.createdAt(),
            route.updatedAt()
        );

        return RouteDto.fromRoute(routeRepository.save(route));
    }
}
