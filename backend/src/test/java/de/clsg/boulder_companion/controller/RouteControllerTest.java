package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.RouteDto;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.service.RouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
@EnableConfigurationProperties(AppProperties.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private RouteDto makeRouteDto(String id, String gymId) {
        return new RouteDto(id, gymId, "TestRoute",
                new Route.Difficulty("6A", "Fontainebleau"),
                "blue", List.of(), "setter-1", "WallA", false, null, List.of(), NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // GET /api/routes
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/routes")
    class GetRoutesByGym {

        @Test
        @DisplayName("returns 200 with routes when gymId is provided")
        @WithMockUser
        void getRoutes_withGymId_returns200WithList() throws Exception {
            when(routeService.getRoutesByGymId("gym-1"))
                    .thenReturn(List.of(makeRouteDto("r1", "gym-1"), makeRouteDto("r2", "gym-1")));

            mockMvc.perform(get("/api/routes").param("gymId", "gym-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("r1"))
                    .andExpect(jsonPath("$[0].gymId").value("gym-1"));
        }

        @Test
        @DisplayName("returns 200 with empty list and does not call service when gymId is absent")
        @WithMockUser
        void getRoutes_noGymId_returns200EmptyListWithoutServiceCall() throws Exception {
            mockMvc.perform(get("/api/routes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(routeService, never()).getRoutesByGymId(any());
        }

        @Test
        @DisplayName("returns 200 with empty list when gym has no routes")
        @WithMockUser
        void getRoutes_gymHasNoRoutes_returns200EmptyList() throws Exception {
            when(routeService.getRoutesByGymId("empty-gym")).thenReturn(List.of());

            mockMvc.perform(get("/api/routes").param("gymId", "empty-gym"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/routes/{id}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/routes/{id}")
    class GetRouteById {

        @Test
        @DisplayName("returns 200 with route body when route exists")
        @WithMockUser
        void getRouteById_existingRoute_returns200WithDto() throws Exception {
            when(routeService.getRouteById("r1")).thenReturn(Optional.of(makeRouteDto("r1", "gym-1")));

            mockMvc.perform(get("/api/routes/r1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("r1"))
                    .andExpect(jsonPath("$.gymId").value("gym-1"))
                    .andExpect(jsonPath("$.name").value("TestRoute"));
        }

        @Test
        @DisplayName("returns 404 when route not found")
        @WithMockUser
        void getRouteById_unknownRoute_returns404() throws Exception {
            when(routeService.getRouteById("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/routes/unknown"))
                    .andExpect(status().isNotFound());
        }
    }
}
