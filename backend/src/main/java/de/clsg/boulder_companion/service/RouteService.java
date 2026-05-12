package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.repository.RouteRepository;
import de.clsg.boulder_companion.dto.RouteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

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
    public RouteDto createRoute(RouteDto routeDto) {
        Route route = routeDto.toRoute();
        return RouteDto.fromRoute(routeRepository.save(route));
    }

    @Transactional
    public RouteDto updateRoute(String id, RouteDto updatedRouteDto) {
        Route existingRoute = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        existingRoute = new Route(
            existingRoute.id(),
            existingRoute.gymId(),
            updatedRouteDto.name(),
            updatedRouteDto.difficulty(),
            updatedRouteDto.holdColor(),
            updatedRouteDto.holdTypes() != null ? updatedRouteDto.holdTypes() : existingRoute.holdTypes(),
            existingRoute.setterId(),
            updatedRouteDto.wall(),
            updatedRouteDto.archived(),
            updatedRouteDto.archived() ? Instant.now() : existingRoute.archivedAt(),
            updatedRouteDto.images() != null ? updatedRouteDto.images() : existingRoute.images(),
            existingRoute.createdAt(),
            Instant.now()
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
