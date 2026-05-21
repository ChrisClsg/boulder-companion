package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.stats.ClimbStatsSummaryDto;
import de.clsg.boulder_companion.model.ClimbLog;
import de.clsg.boulder_companion.repository.ClimbLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StatsService.
 * All repository interactions are mocked.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private ClimbLogRepository climbLogRepository;

    @InjectMocks
    private StatsService statsService;

    private static final Instant NOW = Instant.parse("2024-06-01T10:00:00Z");
    private static final String USER_ID = "u1";

    private ClimbLog makeLog(String id, String routeId, boolean topped, boolean flashed, int attempts) {
        return new ClimbLog(id, USER_ID, "gym-1", routeId, null, topped, flashed, attempts, NOW, null);
    }

    // -------------------------------------------------------------------------
    // getSummary
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getSummary()")
    class GetSummary {

        @Test
        @DisplayName("returns all zeros when user has no logs")
        void getSummary_noLogs_returnsAllZeros() {
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of());

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            assertThat(result.toppedCount()).isZero();
            assertThat(result.toppedPercentage()).isZero();
            assertThat(result.flashedPercentage()).isZero();
            assertThat(result.averageAttemptsPerTop()).isZero();
        }

        @Test
        @DisplayName("single topped route: toppedCount=1, toppedPercentage=100, flashedPercentage=0")
        void getSummary_singleToppedRoute_correctStats() {
            ClimbLog log = makeLog("1", "route-1", true, false, 3);
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of(log));

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            assertThat(result.toppedCount()).isEqualTo(1);
            assertThat(result.toppedPercentage()).isEqualTo(100.0);
            assertThat(result.flashedPercentage()).isZero();
            assertThat(result.averageAttemptsPerTop()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("single flashed route: flashedPercentage=100")
        void getSummary_singleFlashedRoute_flashedPercentage100() {
            ClimbLog log = makeLog("1", "route-1", true, true, 1);
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of(log));

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            assertThat(result.toppedCount()).isEqualTo(1);
            assertThat(result.flashedPercentage()).isEqualTo(100.0);
            assertThat(result.averageAttemptsPerTop()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("mix of topped, flashed, and attempted routes: correct percentages")
        void getSummary_mixedRoutes_correctPercentages() {
            // route-1: topped (not flashed, 4 attempts)
            // route-2: topped and flashed (1 attempt)
            // route-3: attempted but not topped (2 attempts)
            ClimbLog log1 = makeLog("1", "route-1", true, false, 4);
            ClimbLog log2 = makeLog("2", "route-2", true, true, 1);
            ClimbLog log3 = makeLog("3", "route-3", false, false, 2);
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of(log1, log2, log3));

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            // 3 distinct routes attempted, 2 topped
            assertThat(result.toppedCount()).isEqualTo(2);
            assertThat(result.toppedPercentage()).isEqualTo(66.7);
            // 1 flashed out of 2 topped
            assertThat(result.flashedPercentage()).isEqualTo(50.0);
            // (4 + 1) attempts / 2 topped routes = 2.5
            assertThat(result.averageAttemptsPerTop()).isEqualTo(2.5);
        }

        @Test
        @DisplayName("multiple logs on same topped route: grouped correctly")
        void getSummary_multipleLogsOnSameRoute_groupedAsOneRoute() {
            // Two logs on route-1 (e.g. returning visits), topped on second
            ClimbLog log1 = makeLog("1", "route-1", false, false, 3);
            ClimbLog log2 = makeLog("2", "route-1", true, false, 2);
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of(log1, log2));

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            // Only 1 distinct route
            assertThat(result.toppedCount()).isEqualTo(1);
            assertThat(result.toppedPercentage()).isEqualTo(100.0);
            // Total attempts across both logs = 5
            assertThat(result.averageAttemptsPerTop()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("no topped routes: flashedPercentage and averageAttemptsPerTop are zero (no division by zero)")
        void getSummary_noToppedRoutes_noDivisionByZero() {
            ClimbLog log1 = makeLog("1", "route-1", false, false, 2);
            ClimbLog log2 = makeLog("2", "route-2", false, false, 5);
            when(climbLogRepository.findByUserId(USER_ID)).thenReturn(List.of(log1, log2));

            ClimbStatsSummaryDto result = statsService.getSummary(USER_ID);

            assertThat(result.toppedCount()).isZero();
            assertThat(result.toppedPercentage()).isZero();
            assertThat(result.flashedPercentage()).isZero();
            assertThat(result.averageAttemptsPerTop()).isZero();
        }
    }
}
