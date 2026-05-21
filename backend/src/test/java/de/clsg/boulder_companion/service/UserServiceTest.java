package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.dto.RouteDto;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.Gym;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.GymRepository;
import de.clsg.boulder_companion.repository.RouteRepository;
import de.clsg.boulder_companion.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private OAuth2User mockPrincipal() {
        Map<String, Object> attrs = Map.of("id", 42, "login", "alice");
        return new DefaultOAuth2User(List.of(new OAuth2UserAuthority(attrs)), attrs, "login");
    }

    private User makeUser(String id, List<String> favoriteGyms) {
        return new User(id, "42", "Alice", "alice@example.com",
                User.Role.CLIMBER, favoriteGyms, List.of(), List.of(), List.of(), NOW);
    }

    private User makeUserWithRoutes(String id, List<String> favoriteRoutes) {
        return new User(id, "42", "Alice", "alice@example.com",
                User.Role.CLIMBER, List.of(), favoriteRoutes, List.of(), List.of(), NOW);
    }

    private Gym makeGym(String id) {
        return new Gym(id, "Boulderhaus", "Addr", "Desc", "0800", "https://ex.com", null, "admin-1", NOW, NOW);
    }

    private Route makeRoute(String id) {
        return new Route(id, "gym-1", "Crimp Master", new Route.Difficulty("6B", "Font"),
                "Blue", List.of("crimp"), "setter-1", "Wall A",
                false, null, List.of(), NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // getFavoriteGyms
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getFavoriteGyms()")
    class GetFavoriteGyms {

        @Test
        @DisplayName("returns empty list when user has no favorite gyms")
        void getFavoriteGyms_noFavorites_returnsEmptyList() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUser("u-1", List.of()));
            when(gymRepository.findAllById(List.of())).thenReturn(List.of());

            List<GymDto> result = userService.getFavoriteGyms(principal);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns mapped gym DTOs for user's favorite gyms")
        void getFavoriteGyms_withFavorites_returnsMappedDtos() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUser("u-1", List.of("g1", "g2")));
            when(gymRepository.findAllById(List.of("g1", "g2")))
                    .thenReturn(List.of(makeGym("g1"), makeGym("g2")));

            List<GymDto> result = userService.getFavoriteGyms(principal);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(GymDto::id).containsExactlyInAnyOrder("g1", "g2");
        }

        @Test
        @DisplayName("handles null favoriteGyms list defensively")
        void getFavoriteGyms_nullFavoriteGyms_returnsEmptyList() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUser("u-1", null));
            when(gymRepository.findAllById(List.of())).thenReturn(List.of());

            List<GymDto> result = userService.getFavoriteGyms(principal);

            assertThat(result).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // addFavoriteGym
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("addFavoriteGym()")
    class AddFavoriteGym {

        @Test
        @DisplayName("throws 404 ResponseStatusException when gym does not exist")
        void addFavoriteGym_gymNotFound_throws404() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUser("u-1", List.of()));
            when(gymRepository.existsById("missing-gym")).thenReturn(false);

            assertThatThrownBy(() -> userService.addFavoriteGym(principal, "missing-gym"))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode().value())
                    .isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("saves user with appended gym and returns DTO when gym exists")
        void addFavoriteGym_gymExists_savesUserAndReturnsDto() {
            OAuth2User principal = mockPrincipal();
            User original = makeUser("u-1", List.of());
            User updated = makeUser("u-1", List.of("g1"));
            when(authService.getOrCreateUser(principal)).thenReturn(original);
            when(gymRepository.existsById("g1")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(updated);

            UserDto result = userService.addFavoriteGym(principal, "g1");

            assertThat(result.favoriteGyms()).containsExactly("g1");
            verify(userRepository).save(argThat(u -> u.favoriteGyms().contains("g1")));
        }
    }

    // -------------------------------------------------------------------------
    // removeFavoriteGym
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("removeFavoriteGym()")
    class RemoveFavoriteGym {

        @Test
        @DisplayName("saves user without removed gym and returns DTO")
        void removeFavoriteGym_gymInFavorites_savesUserAndReturnsDto() {
            OAuth2User principal = mockPrincipal();
            User original = makeUser("u-1", List.of("g1"));
            User updated = makeUser("u-1", List.of());
            when(authService.getOrCreateUser(principal)).thenReturn(original);
            when(userRepository.save(any(User.class))).thenReturn(updated);

            UserDto result = userService.removeFavoriteGym(principal, "g1");

            assertThat(result.favoriteGyms()).isEmpty();
            verify(userRepository).save(argThat(u -> !u.favoriteGyms().contains("g1")));
        }
    }

    // -------------------------------------------------------------------------
    // getFavoriteRoutes
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getFavoriteRoutes()")
    class GetFavoriteRoutes {

        @Test
        @DisplayName("returns empty list when user has no favorite routes")
        void getFavoriteRoutes_noFavorites_returnsEmptyList() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUserWithRoutes("u-1", List.of()));
            when(routeRepository.findAllById(List.of())).thenReturn(List.of());

            List<RouteDto> result = userService.getFavoriteRoutes(principal);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns mapped route DTOs for user's favorite routes")
        void getFavoriteRoutes_withFavorites_returnsMappedDtos() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUserWithRoutes("u-1", List.of("r1", "r2")));
            when(routeRepository.findAllById(List.of("r1", "r2")))
                    .thenReturn(List.of(makeRoute("r1"), makeRoute("r2")));

            List<RouteDto> result = userService.getFavoriteRoutes(principal);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(RouteDto::id).containsExactlyInAnyOrder("r1", "r2");
        }

        @Test
        @DisplayName("handles null favoriteRoutes list defensively")
        void getFavoriteRoutes_nullFavoriteRoutes_returnsEmptyList() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUserWithRoutes("u-1", null));
            when(routeRepository.findAllById(List.of())).thenReturn(List.of());

            List<RouteDto> result = userService.getFavoriteRoutes(principal);

            assertThat(result).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // addFavoriteRoute
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("addFavoriteRoute()")
    class AddFavoriteRoute {

        @Test
        @DisplayName("throws 404 ResponseStatusException when route does not exist")
        void addFavoriteRoute_routeNotFound_throws404() {
            OAuth2User principal = mockPrincipal();
            when(authService.getOrCreateUser(principal)).thenReturn(makeUserWithRoutes("u-1", List.of()));
            when(routeRepository.existsById("missing-route")).thenReturn(false);

            assertThatThrownBy(() -> userService.addFavoriteRoute(principal, "missing-route"))
                    .isInstanceOf(ResponseStatusException.class)
                    .extracting(e -> ((ResponseStatusException) e).getStatusCode().value())
                    .isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("saves user with appended route and returns DTO when route exists")
        void addFavoriteRoute_routeExists_savesUserAndReturnsDto() {
            OAuth2User principal = mockPrincipal();
            User original = makeUserWithRoutes("u-1", List.of());
            User updated = makeUserWithRoutes("u-1", List.of("r1"));
            when(authService.getOrCreateUser(principal)).thenReturn(original);
            when(routeRepository.existsById("r1")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(updated);

            UserDto result = userService.addFavoriteRoute(principal, "r1");

            assertThat(result.favoriteRoutes()).containsExactly("r1");
            verify(userRepository).save(argThat(u -> u.favoriteRoutes().contains("r1")));
        }
    }

    // -------------------------------------------------------------------------
    // removeFavoriteRoute
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("removeFavoriteRoute()")
    class RemoveFavoriteRoute {

        @Test
        @DisplayName("saves user without removed route and returns DTO")
        void removeFavoriteRoute_routeInFavorites_savesUserAndReturnsDto() {
            OAuth2User principal = mockPrincipal();
            User original = makeUserWithRoutes("u-1", List.of("r1"));
            User updated = makeUserWithRoutes("u-1", List.of());
            when(authService.getOrCreateUser(principal)).thenReturn(original);
            when(userRepository.save(any(User.class))).thenReturn(updated);

            UserDto result = userService.removeFavoriteRoute(principal, "r1");

            assertThat(result.favoriteRoutes()).isEmpty();
            verify(userRepository).save(argThat(u -> !u.favoriteRoutes().contains("r1")));
        }
    }
}
