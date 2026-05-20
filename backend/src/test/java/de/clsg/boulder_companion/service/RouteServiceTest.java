package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.RouteDto;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.repository.RouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RouteService.
 * RouteRepository is mocked; only mapping/delegation logic is tested.
 */
@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private Route makeRoute(String id, String gymId, String name) {
        return new Route(id, gymId, name,
                new Route.Difficulty("6A", "Fontainebleau"),
                "yellow", List.of("crimp"), "setter-1",
                "wall-A", false, null, List.of(), NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // getRoutesByGymId
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getRoutesByGymId()")
    class GetRoutesByGymId {

        @Test
        @DisplayName("returns all routes for a given gymId")
        void getRoutesByGymId_existingGym_returnsRoutes() {
            Route r1 = makeRoute("r1", "gym-1", "Route Alpha");
            Route r2 = makeRoute("r2", "gym-1", "Route Beta");
            when(routeRepository.findByGymId("gym-1")).thenReturn(List.of(r1, r2));

            List<RouteDto> result = routeService.getRoutesByGymId("gym-1");

            assertThat(result).hasSize(2);
            assertThat(result).extracting(RouteDto::name).containsExactly("Route Alpha", "Route Beta");
        }

        @Test
        @DisplayName("returns empty list when no routes found for gym")
        void getRoutesByGymId_noRoutes_returnsEmptyList() {
            when(routeRepository.findByGymId("gym-empty")).thenReturn(List.of());

            List<RouteDto> result = routeService.getRoutesByGymId("gym-empty");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maps route fields correctly including null holdTypes and images")
        void getRoutesByGymId_routeWithNullCollections_mapsDefensively() {
            Route route = new Route("r1", "gym-1", "Route Alpha",
                    new Route.Difficulty("6A", "Fontainebleau"),
                    "yellow", null, "setter-1", "wall-A", false, null, null, NOW, NOW);
            when(routeRepository.findByGymId("gym-1")).thenReturn(List.of(route));

            List<RouteDto> result = routeService.getRoutesByGymId("gym-1");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).holdTypes()).isEmpty();
            assertThat(result.get(0).images()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // getRouteById
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getRouteById()")
    class GetRouteById {

        @Test
        @DisplayName("returns the route DTO when found")
        void getRouteById_existingId_returnsRouteDto() {
            Route route = makeRoute("r1", "gym-1", "The Overhang");
            when(routeRepository.findById("r1")).thenReturn(Optional.of(route));

            Optional<RouteDto> result = routeService.getRouteById("r1");

            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo("r1");
            assertThat(result.get().name()).isEqualTo("The Overhang");
            assertThat(result.get().gymId()).isEqualTo("gym-1");
        }

        @Test
        @DisplayName("returns empty Optional when route not found")
        void getRouteById_unknownId_returnsEmptyOptional() {
            when(routeRepository.findById("unknown")).thenReturn(Optional.empty());

            Optional<RouteDto> result = routeService.getRouteById("unknown");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("archived routes are returned normally")
        void getRouteById_archivedRoute_returnsArchivedRouteDto() {
            Route archived = new Route("r1", "gym-1", "Old Route",
                    new Route.Difficulty("7A", "Fontainebleau"),
                    "blue", List.of(), "setter-1", "wall-B", true, NOW, List.of(), NOW, NOW);
            when(routeRepository.findById("r1")).thenReturn(Optional.of(archived));

            Optional<RouteDto> result = routeService.getRouteById("r1");

            assertThat(result).isPresent();
            assertThat(result.get().archived()).isTrue();
            assertThat(result.get().archivedAt()).isEqualTo(NOW);
        }
    }
}
