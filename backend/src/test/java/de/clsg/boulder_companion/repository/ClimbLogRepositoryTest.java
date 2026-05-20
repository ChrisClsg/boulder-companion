package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.ClimbLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ClimbLogRepository using Flapdoodle embedded MongoDB.
 * Tests the custom derived query methods.
 */
@SpringBootTest
class ClimbLogRepositoryTest {

    @Autowired
    private ClimbLogRepository climbLogRepository;

    private static final Instant T1 = Instant.parse("2024-05-01T10:00:00Z");
    private static final Instant T2 = Instant.parse("2024-06-01T10:00:00Z");
    private static final Instant T3 = Instant.parse("2024-07-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        climbLogRepository.deleteAll();
    }

    private ClimbLog save(String userId, String gymId, String routeId, String sessionId,
                          boolean topped, boolean flashed, int attempts, Instant climbedAt) {
        return climbLogRepository.save(
                new ClimbLog(null, userId, gymId, routeId, sessionId, topped, flashed, attempts, climbedAt, null));
    }

    // -------------------------------------------------------------------------
    // findByUserId
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("findByUserId()")
    class FindByUserId {

        @Test
        @DisplayName("returns all logs for the given userId")
        void findByUserId_multipleLogsForUser_returnsAllUserLogs() {
            save("u1", "gym-1", "route-1", null, true, false, 3, T1);
            save("u1", "gym-1", "route-2", null, false, false, 2, T2);
            save("u2", "gym-1", "route-1", null, true, true, 1, T1);

            List<ClimbLog> result = climbLogRepository.findByUserId("u1");

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ClimbLog::userId).containsOnly("u1");
        }

        @Test
        @DisplayName("returns empty list when user has no logs")
        void findByUserId_noLogsForUser_returnsEmptyList() {
            save("u1", "gym-1", "route-1", null, true, false, 1, T1);

            List<ClimbLog> result = climbLogRepository.findByUserId("u99");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("does not return logs of other users")
        void findByUserId_onlyReturnsOwnLogs() {
            save("u1", "gym-1", "route-1", null, true, false, 1, T1);
            save("u2", "gym-1", "route-1", null, true, false, 1, T1);

            List<ClimbLog> result = climbLogRepository.findByUserId("u1");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).userId()).isEqualTo("u1");
        }

        @Test
        @DisplayName("returns all fields correctly")
        void findByUserId_allFieldsPersistedAndRetrieved() {
            save("u1", "gym-A", "route-X", "sess-1", true, true, 1, T2);

            List<ClimbLog> result = climbLogRepository.findByUserId("u1");

            assertThat(result).hasSize(1);
            ClimbLog log = result.get(0);
            assertThat(log.gymId()).isEqualTo("gym-A");
            assertThat(log.routeId()).isEqualTo("route-X");
            assertThat(log.sessionId()).isEqualTo("sess-1");
            assertThat(log.topped()).isTrue();
            assertThat(log.flashed()).isTrue();
            assertThat(log.attempts()).isEqualTo(1);
            assertThat(log.climbedAt()).isEqualTo(T2);
        }
    }

    // -------------------------------------------------------------------------
    // existsByUserIdAndRouteIdAndClimbedAtBefore
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("existsByUserIdAndRouteIdAndClimbedAtBefore()")
    class ExistsByUserIdAndRouteIdAndClimbedAtBefore {

        @Test
        @DisplayName("returns true when a prior log exists for userId and routeId before the given instant")
        void existsBy_priorLogExists_returnsTrue() {
            save("u1", "gym-1", "route-1", null, false, false, 2, T1);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("returns false when no log exists before the given instant")
        void existsBy_noLogBefore_returnsFalse() {
            save("u1", "gym-1", "route-1", null, true, false, 1, T3);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("returns false when log exists at exactly the given instant (boundary: exclusive)")
        void existsBy_logAtExactInstant_returnsFalse() {
            save("u1", "gym-1", "route-1", null, true, false, 1, T2);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("returns false when no logs exist at all")
        void existsBy_noLogs_returnsFalse() {
            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("returns false when prior log exists but for different userId")
        void existsBy_differentUser_returnsFalse() {
            save("u2", "gym-1", "route-1", null, false, false, 1, T1);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("returns false when prior log exists but for different routeId")
        void existsBy_differentRoute_returnsFalse() {
            save("u1", "gym-1", "route-OTHER", null, false, false, 1, T1);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("returns true when multiple prior logs exist — any one suffices")
        void existsBy_multiplePriorLogs_returnsTrue() {
            save("u1", "gym-1", "route-1", null, false, false, 1, T1);
            save("u1", "gym-1", "route-1", null, false, false, 2, T1);

            boolean result = climbLogRepository
                    .existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", T2);

            assertThat(result).isTrue();
        }
    }
}
