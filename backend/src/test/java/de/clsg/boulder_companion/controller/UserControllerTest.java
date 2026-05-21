package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.dto.RouteDto;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@EnableConfigurationProperties(AppProperties.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private GymDto makeGymDto(String id, String name) {
        return new GymDto(id, name, "Address", "Desc", "0800", "https://ex.com", null, "admin-1", NOW, NOW);
    }

    private UserDto makeUserDto(String id) {
        return new UserDto(id, "42", "Alice", "alice@example.com",
                User.Role.CLIMBER, List.of("gym-1"), List.of(), List.of(), List.of());
    }

    private RouteDto makeRouteDto(String id) {
        return new RouteDto(id, "gym-1", "Crimp Master",
                new Route.Difficulty("6B", "Font"),
                "Blue", List.of("crimp"), "setter-1", "Wall A",
                false, null, List.of(), NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // GET /api/users/me/favorite-gyms
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/users/me/favorite-gyms")
    class GetFavoriteGyms {

        @Test
        @DisplayName("returns 200 with gym list when user has favorites")
        void getFavoriteGyms_withFavorites_returns200WithList() throws Exception {
            when(userService.getFavoriteGyms(any())).thenReturn(List.of(makeGymDto("g1", "Boulderhaus")));

            mockMvc.perform(get("/api/users/me/favorite-gyms")
                            .with(oidcLogin().userInfoToken(token -> token
                                    .claim("id", "42")
                                    .claim("login", "alice"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value("g1"))
                    .andExpect(jsonPath("$[0].name").value("Boulderhaus"));
        }

        @Test
        @DisplayName("returns 200 with empty list when user has no favorites")
        void getFavoriteGyms_noFavorites_returns200EmptyList() throws Exception {
            when(userService.getFavoriteGyms(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/users/me/favorite-gyms")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("returns 3xx redirect when unauthenticated")
        void getFavoriteGyms_unauthenticated_returnsRedirect() throws Exception {
            mockMvc.perform(get("/api/users/me/favorite-gyms"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/users/me/favorite-gyms/{gymId}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PUT /api/users/me/favorite-gyms/{gymId}")
    class AddFavoriteGym {

        @Test
        @DisplayName("returns 200 with updated user DTO when gym exists")
        void addFavoriteGym_gymExists_returns200WithDto() throws Exception {
            UserDto dto = makeUserDto("u-1");
            when(userService.addFavoriteGym(any(), eq("gym-1"))).thenReturn(dto);

            mockMvc.perform(put("/api/users/me/favorite-gyms/gym-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u-1"))
                    .andExpect(jsonPath("$.name").value("Alice"));
        }

        @Test
        @DisplayName("returns 404 when gym does not exist")
        void addFavoriteGym_gymNotFound_returns404() throws Exception {
            when(userService.addFavoriteGym(any(), any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

            mockMvc.perform(put("/api/users/me/favorite-gyms/missing-gym")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 403 when CSRF token is missing")
        void addFavoriteGym_missingCsrf_returns403() throws Exception {
            mockMvc.perform(put("/api/users/me/favorite-gyms/gym-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isForbidden());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/users/me/favorite-gyms/{gymId}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("DELETE /api/users/me/favorite-gyms/{gymId}")
    class RemoveFavoriteGym {

        @Test
        @DisplayName("returns 200 with updated user DTO on successful removal")
        void removeFavoriteGym_gymInFavorites_returns200WithDto() throws Exception {
            UserDto dto = makeUserDto("u-1");
            when(userService.removeFavoriteGym(any(), eq("gym-1"))).thenReturn(dto);

            mockMvc.perform(delete("/api/users/me/favorite-gyms/gym-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u-1"));
        }

        @Test
        @DisplayName("returns 403 when CSRF token is missing")
        void removeFavoriteGym_missingCsrf_returns403() throws Exception {
            mockMvc.perform(delete("/api/users/me/favorite-gyms/gym-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isForbidden());
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/users/me/favorite-routes
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/users/me/favorite-routes")
    class GetFavoriteRoutes {

        @Test
        @DisplayName("returns 200 with route list when user has favorites")
        void getFavoriteRoutes_withFavorites_returns200WithList() throws Exception {
            when(userService.getFavoriteRoutes(any())).thenReturn(List.of(makeRouteDto("r1")));

            mockMvc.perform(get("/api/users/me/favorite-routes")
                            .with(oidcLogin().userInfoToken(token -> token
                                    .claim("id", "42")
                                    .claim("login", "alice"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value("r1"))
                    .andExpect(jsonPath("$[0].name").value("Crimp Master"));
        }

        @Test
        @DisplayName("returns 200 with empty list when user has no favorite routes")
        void getFavoriteRoutes_noFavorites_returns200EmptyList() throws Exception {
            when(userService.getFavoriteRoutes(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/users/me/favorite-routes")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("returns 3xx redirect when unauthenticated")
        void getFavoriteRoutes_unauthenticated_returnsRedirect() throws Exception {
            mockMvc.perform(get("/api/users/me/favorite-routes"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/users/me/favorite-routes/{routeId}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PUT /api/users/me/favorite-routes/{routeId}")
    class AddFavoriteRoute {

        @Test
        @DisplayName("returns 200 with updated user DTO when route exists")
        void addFavoriteRoute_routeExists_returns200WithDto() throws Exception {
            UserDto dto = makeUserDto("u-1");
            when(userService.addFavoriteRoute(any(), eq("route-1"))).thenReturn(dto);

            mockMvc.perform(put("/api/users/me/favorite-routes/route-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u-1"))
                    .andExpect(jsonPath("$.name").value("Alice"));
        }

        @Test
        @DisplayName("returns 404 when route does not exist")
        void addFavoriteRoute_routeNotFound_returns404() throws Exception {
            when(userService.addFavoriteRoute(any(), any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));

            mockMvc.perform(put("/api/users/me/favorite-routes/missing-route")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 403 when CSRF token is missing")
        void addFavoriteRoute_missingCsrf_returns403() throws Exception {
            mockMvc.perform(put("/api/users/me/favorite-routes/route-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isForbidden());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/users/me/favorite-routes/{routeId}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("DELETE /api/users/me/favorite-routes/{routeId}")
    class RemoveFavoriteRoute {

        @Test
        @DisplayName("returns 200 with updated user DTO on successful removal")
        void removeFavoriteRoute_routeInFavorites_returns200WithDto() throws Exception {
            UserDto dto = makeUserDto("u-1");
            when(userService.removeFavoriteRoute(any(), eq("route-1"))).thenReturn(dto);

            mockMvc.perform(delete("/api/users/me/favorite-routes/route-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42")))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u-1"));
        }

        @Test
        @DisplayName("returns 403 when CSRF token is missing")
        void removeFavoriteRoute_missingCsrf_returns403() throws Exception {
            mockMvc.perform(delete("/api/users/me/favorite-routes/route-1")
                            .with(oidcLogin().userInfoToken(token -> token.claim("id", "42"))))
                    .andExpect(status().isForbidden());
        }
    }
}
