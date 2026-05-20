package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.feedback.RouteFeedbackDto;
import de.clsg.boulder_companion.dto.feedback.UpsertRouteFeedbackRequest;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.model.RouteFeedback;
import de.clsg.boulder_companion.model.RouteFeedback.DifficultyFeedback;
import de.clsg.boulder_companion.repository.RouteFeedbackRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RouteFeedbackService.
 * Both repositories are mocked.
 */
@ExtendWith(MockitoExtension.class)
class RouteFeedbackServiceTest {

    @Mock
    private RouteFeedbackRepository routeFeedbackRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteFeedbackService routeFeedbackService;

    private static final Instant NOW = Instant.parse("2024-03-15T12:00:00Z");

    private Route makeRoute(String id, String gymId) {
        return new Route(id, gymId, "Test Route",
                new Route.Difficulty("6B", "Fontainebleau"),
                "red", List.of(), "setter-1", "wall-C", false, null, List.of(), NOW, NOW);
    }

    private RouteFeedback makeFeedback(String id, String userId, String routeId, String gymId,
                                        int rating, DifficultyFeedback difficulty) {
        return new RouteFeedback(id, userId, routeId, gymId, rating, difficulty, NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // getMyFeedback
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getMyFeedback()")
    class GetMyFeedback {

        @Test
        @DisplayName("returns feedback DTO when feedback exists")
        void getMyFeedback_feedbackExists_returnsMappedDto() {
            RouteFeedback feedback = makeFeedback("fb-1", "u1", "route-1", "gym-1", 4, DifficultyFeedback.ABOUT_RIGHT);
            when(routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1"))
                    .thenReturn(Optional.of(feedback));

            Optional<RouteFeedbackDto> result = routeFeedbackService.getMyFeedback("u1", "route-1");

            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo("fb-1");
            assertThat(result.get().userId()).isEqualTo("u1");
            assertThat(result.get().routeId()).isEqualTo("route-1");
            assertThat(result.get().userRating()).isEqualTo(4);
            assertThat(result.get().difficultyFeedback()).isEqualTo(DifficultyFeedback.ABOUT_RIGHT);
        }

        @Test
        @DisplayName("returns empty Optional when no feedback exists")
        void getMyFeedback_noFeedback_returnsEmpty() {
            when(routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1"))
                    .thenReturn(Optional.empty());

            Optional<RouteFeedbackDto> result = routeFeedbackService.getMyFeedback("u1", "route-1");

            assertThat(result).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // upsertMyFeedback
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("upsertMyFeedback()")
    class UpsertMyFeedback {

        @Test
        @DisplayName("creates new feedback when none exists")
        void upsertMyFeedback_noExistingFeedback_createsNew() {
            Route route = makeRoute("route-1", "gym-1");
            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(5, DifficultyFeedback.EASIER);

            when(routeRepository.findById("route-1")).thenReturn(Optional.of(route));
            when(routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1"))
                    .thenReturn(Optional.empty());

            RouteFeedback savedFeedback = makeFeedback("fb-new", "u1", "route-1", "gym-1", 5, DifficultyFeedback.EASIER);
            when(routeFeedbackRepository.save(any(RouteFeedback.class))).thenReturn(savedFeedback);

            RouteFeedbackDto result = routeFeedbackService.upsertMyFeedback("u1", "route-1", request);

            assertThat(result.id()).isEqualTo("fb-new");
            assertThat(result.userRating()).isEqualTo(5);
            assertThat(result.difficultyFeedback()).isEqualTo(DifficultyFeedback.EASIER);
            assertThat(result.gymId()).isEqualTo("gym-1");

            // The new feedback should have null id (to trigger insert)
            verify(routeFeedbackRepository).save(argThat(fb -> fb.id() == null && fb.userId().equals("u1")));
        }

        @Test
        @DisplayName("updates existing feedback preserving id and createdAt")
        void upsertMyFeedback_existingFeedback_updatesFields() {
            Route route = makeRoute("route-1", "gym-1");
            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(3, DifficultyFeedback.HARDER);

            RouteFeedback existing = makeFeedback("fb-existing", "u1", "route-1", "gym-1", 1, DifficultyFeedback.MUCH_EASIER);
            when(routeRepository.findById("route-1")).thenReturn(Optional.of(route));
            when(routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1"))
                    .thenReturn(Optional.of(existing));

            RouteFeedback savedFeedback = makeFeedback("fb-existing", "u1", "route-1", "gym-1", 3, DifficultyFeedback.HARDER);
            when(routeFeedbackRepository.save(any(RouteFeedback.class))).thenReturn(savedFeedback);

            RouteFeedbackDto result = routeFeedbackService.upsertMyFeedback("u1", "route-1", request);

            assertThat(result.id()).isEqualTo("fb-existing");
            assertThat(result.userRating()).isEqualTo(3);
            assertThat(result.difficultyFeedback()).isEqualTo(DifficultyFeedback.HARDER);

            // Should preserve existing id and createdAt
            verify(routeFeedbackRepository).save(argThat(fb ->
                    "fb-existing".equals(fb.id()) &&
                    fb.userRating() == 3 &&
                    fb.createdAt() == existing.createdAt()
            ));
        }

        @Test
        @DisplayName("throws RuntimeException when route not found")
        void upsertMyFeedback_routeNotFound_throwsRuntimeException() {
            when(routeRepository.findById("missing-route")).thenReturn(Optional.empty());

            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(4, DifficultyFeedback.ABOUT_RIGHT);

            assertThatThrownBy(() -> routeFeedbackService.upsertMyFeedback("u1", "missing-route", request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Route not found");

            verify(routeFeedbackRepository, never()).save(any());
        }

        @Test
        @DisplayName("gymId is taken from the route, not from request")
        void upsertMyFeedback_gymIdTakenFromRoute() {
            Route route = makeRoute("route-1", "the-correct-gym");
            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(2, DifficultyFeedback.MUCH_HARDER);

            when(routeRepository.findById("route-1")).thenReturn(Optional.of(route));
            when(routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1"))
                    .thenReturn(Optional.empty());

            RouteFeedback saved = makeFeedback("fb-1", "u1", "route-1", "the-correct-gym", 2, DifficultyFeedback.MUCH_HARDER);
            when(routeFeedbackRepository.save(any())).thenReturn(saved);

            RouteFeedbackDto result = routeFeedbackService.upsertMyFeedback("u1", "route-1", request);

            assertThat(result.gymId()).isEqualTo("the-correct-gym");
        }
    }
}
