package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.repository.RouteRepository;
import de.clsg.boulder_companion.dto.RouteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
