package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.RouteFeedback;
import de.clsg.boulder_companion.model.RouteFeedback.DifficultyFeedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for RouteFeedbackRepository using Flapdoodle embedded MongoDB.
 * Tests the custom derived query method findByUserIdAndRouteId.
 */
@SpringBootTest
class RouteFeedbackRepositoryTest {

    @Autowired
    private RouteFeedbackRepository routeFeedbackRepository;

    private static final Instant NOW = Instant.parse("2024-04-01T12:00:00Z");

    @BeforeEach
    void setUp() {
        routeFeedbackRepository.deleteAll();
    }

    private RouteFeedback save(String userId, String routeId, String gymId,
                                int rating, DifficultyFeedback difficulty) {
        return routeFeedbackRepository.save(
                new RouteFeedback(null, userId, routeId, gymId, rating, difficulty, NOW, NOW));
    }

    // -------------------------------------------------------------------------
    // findByUserIdAndRouteId
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("findByUserIdAndRouteId()")
    class FindByUserIdAndRouteId {

        @Test
        @DisplayName("returns feedback when exact userId and routeId match")
        void findByUserIdAndRouteId_exactMatch_returnsFeedback() {
            save("u1", "route-1", "gym-1", 4, DifficultyFeedback.ABOUT_RIGHT);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1");

            assertThat(result).isPresent();
            assertThat(result.get().userId()).isEqualTo("u1");
            assertThat(result.get().routeId()).isEqualTo("route-1");
            assertThat(result.get().userRating()).isEqualTo(4);
            assertThat(result.get().difficultyFeedback()).isEqualTo(DifficultyFeedback.ABOUT_RIGHT);
        }

        @Test
        @DisplayName("returns empty Optional when no feedback exists for userId+routeId")
        void findByUserIdAndRouteId_noMatch_returnsEmpty() {
            save("u1", "route-1", "gym-1", 3, DifficultyFeedback.HARDER);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u2", "route-1");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty Optional when routeId does not match")
        void findByUserIdAndRouteId_wrongRouteId_returnsEmpty() {
            save("u1", "route-1", "gym-1", 2, DifficultyFeedback.EASIER);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-99");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty Optional when no feedback exists at all")
        void findByUserIdAndRouteId_emptyRepo_returnsEmpty() {
            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns correct feedback when multiple users have given feedback on same route")
        void findByUserIdAndRouteId_multipleUsers_returnsCorrectUserFeedback() {
            save("u1", "route-1", "gym-1", 5, DifficultyFeedback.MUCH_HARDER);
            save("u2", "route-1", "gym-1", 2, DifficultyFeedback.MUCH_EASIER);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u2", "route-1");

            assertThat(result).isPresent();
            assertThat(result.get().userId()).isEqualTo("u2");
            assertThat(result.get().userRating()).isEqualTo(2);
            assertThat(result.get().difficultyFeedback()).isEqualTo(DifficultyFeedback.MUCH_EASIER);
        }

        @Test
        @DisplayName("returns correct feedback when same user has given feedback on different routes")
        void findByUserIdAndRouteId_sameUserMultipleRoutes_returnsCorrectRouteFeedback() {
            save("u1", "route-1", "gym-1", 3, DifficultyFeedback.ABOUT_RIGHT);
            save("u1", "route-2", "gym-1", 1, DifficultyFeedback.MUCH_HARDER);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-2");

            assertThat(result).isPresent();
            assertThat(result.get().routeId()).isEqualTo("route-2");
            assertThat(result.get().userRating()).isEqualTo(1);
        }

        @Test
        @DisplayName("persists and retrieves all RouteFeedback fields correctly")
        void findByUserIdAndRouteId_allFieldsPersisted() {
            save("u1", "route-1", "gym-A", 5, DifficultyFeedback.EASIER);

            Optional<RouteFeedback> result = routeFeedbackRepository.findByUserIdAndRouteId("u1", "route-1");

            assertThat(result).isPresent();
            RouteFeedback fb = result.get();
            assertThat(fb.id()).isNotNull();
            assertThat(fb.gymId()).isEqualTo("gym-A");
            assertThat(fb.userRating()).isEqualTo(5);
            assertThat(fb.difficultyFeedback()).isEqualTo(DifficultyFeedback.EASIER);
            assertThat(fb.createdAt()).isNotNull();
        }
    }
}
