package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.climblog.ClimbLogDto;
import de.clsg.boulder_companion.dto.climblog.CreateClimbLogRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClimbLogService.
 * All repository interactions are mocked.
 */
@ExtendWith(MockitoExtension.class)
class ClimbLogServiceTest {

    @Mock
    private ClimbLogRepository climbLogRepository;

    @InjectMocks
    private ClimbLogService climbLogService;

    private static final Instant NOW = Instant.parse("2024-06-01T10:00:00Z");
    private static final Instant EARLIER = Instant.parse("2024-05-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2024-07-01T10:00:00Z");

    private ClimbLog makeLog(String id, String userId, String gymId, String routeId,
                             String sessionId, boolean topped, boolean flashed, int attempts,
                             Instant climbedAt) {
        return new ClimbLog(id, userId, gymId, routeId, sessionId, topped, flashed, attempts, climbedAt, null);
    }

    // -------------------------------------------------------------------------
    // getClimbLogs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getClimbLogs()")
    class GetClimbLogs {

        @Test
        @DisplayName("returns all logs for userId when no filters applied")
        void getClimbLogs_noFilters_returnsAllUserLogs() {
            ClimbLog log1 = makeLog("1", "u1", "gym-1", "route-1", null, true, false, 3, NOW);
            ClimbLog log2 = makeLog("2", "u1", "gym-2", "route-2", null, false, false, 2, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(log1, log2));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, null, null, null, null);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(ClimbLogDto::id).containsExactly("1", "2");
        }

        @Test
        @DisplayName("filters by gymId")
        void getClimbLogs_gymIdFilter_returnsOnlyMatchingGym() {
            ClimbLog log1 = makeLog("1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            ClimbLog log2 = makeLog("2", "u1", "gym-2", "route-2", null, false, false, 2, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(log1, log2));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", "gym-1", null, null, null, null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).gymId()).isEqualTo("gym-1");
        }

        @Test
        @DisplayName("filters by routeId")
        void getClimbLogs_routeIdFilter_returnsOnlyMatchingRoute() {
            ClimbLog log1 = makeLog("1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            ClimbLog log2 = makeLog("2", "u1", "gym-1", "route-2", null, false, false, 2, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(log1, log2));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, "route-2", null, null, null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).routeId()).isEqualTo("route-2");
        }

        @Test
        @DisplayName("filters by sessionId")
        void getClimbLogs_sessionIdFilter_returnsOnlyMatchingSession() {
            ClimbLog log1 = makeLog("1", "u1", "gym-1", "route-1", "sess-A", true, false, 1, NOW);
            ClimbLog log2 = makeLog("2", "u1", "gym-1", "route-2", "sess-B", false, false, 2, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(log1, log2));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, "sess-A", null, null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).sessionId()).isEqualTo("sess-A");
        }

        @Test
        @DisplayName("filters by topped=true")
        void getClimbLogs_toppedTrueFilter_returnsOnlyToppedLogs() {
            ClimbLog log1 = makeLog("1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            ClimbLog log2 = makeLog("2", "u1", "gym-1", "route-2", null, false, false, 2, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(log1, log2));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, null, true, null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).topped()).isTrue();
        }

        @Test
        @DisplayName("filters by from timestamp (inclusive)")
        void getClimbLogs_fromFilter_excludesEarlierLogs() {
            Instant borderline = Instant.parse("2024-06-01T00:00:00Z");
            ClimbLog oldLog = makeLog("1", "u1", "gym-1", "route-1", null, false, false, 1, EARLIER);
            ClimbLog newLog = makeLog("2", "u1", "gym-1", "route-2", null, false, false, 1, NOW);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(oldLog, newLog));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, null, null, borderline, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo("2");
        }

        @Test
        @DisplayName("filters by to timestamp (exclusive)")
        void getClimbLogs_toFilter_excludesLogsAtOrAfterTo() {
            ClimbLog earlyLog = makeLog("1", "u1", "gym-1", "route-1", null, false, false, 1, EARLIER);
            ClimbLog lateLog = makeLog("2", "u1", "gym-1", "route-2", null, false, false, 1, LATER);
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of(earlyLog, lateLog));

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, null, null, null, NOW);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo("1");
        }

        @Test
        @DisplayName("returns empty list when no logs found for user")
        void getClimbLogs_noLogs_returnsEmptyList() {
            when(climbLogRepository.findByUserId("u1")).thenReturn(List.of());

            List<ClimbLogDto> result = climbLogService.getClimbLogs("u1", null, null, null, null, null, null);

            assertThat(result).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // createClimbLog
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("createClimbLog()")
    class CreateClimbLog {

        @Test
        @DisplayName("happy path: topped with multiple attempts, no prior logs — not flashed")
        void createClimbLog_toppedMultipleAttempts_noPriorLogs_notFlashed() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 3, NOW);

            when(climbLogRepository.existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", NOW))
                    .thenReturn(false);
            ClimbLog saved = makeLog("log-1", "u1", "gym-1", "route-1", null, true, false, 3, NOW);
            when(climbLogRepository.save(any(ClimbLog.class))).thenReturn(saved);

            ClimbLogDto result = climbLogService.createClimbLog("u1", request);

            assertThat(result.flashed()).isFalse();
            assertThat(result.topped()).isTrue();
            assertThat(result.attempts()).isEqualTo(3);
        }

        @Test
        @DisplayName("flashed: topped + attempts==1 + no prior logs")
        void createClimbLog_toppedSingleAttemptNoPriorLogs_flashed() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 1, NOW);

            when(climbLogRepository.existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", NOW))
                    .thenReturn(false);
            ClimbLog saved = makeLog("log-1", "u1", "gym-1", "route-1", null, true, true, 1, NOW);
            when(climbLogRepository.save(any(ClimbLog.class))).thenReturn(saved);

            ClimbLogDto result = climbLogService.createClimbLog("u1", request);

            assertThat(result.flashed()).isTrue();

            // Verify the ClimbLog passed to save had flashed=true
            verify(climbLogRepository).save(argThat(log -> log.flashed()));
        }

        @Test
        @DisplayName("not flashed: topped + attempts==1 but prior logs exist")
        void createClimbLog_toppedSingleAttemptWithPriorLogs_notFlashed() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 1, NOW);

            when(climbLogRepository.existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", NOW))
                    .thenReturn(true);
            ClimbLog saved = makeLog("log-1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            when(climbLogRepository.save(any(ClimbLog.class))).thenReturn(saved);

            ClimbLogDto result = climbLogService.createClimbLog("u1", request);

            assertThat(result.flashed()).isFalse();
            verify(climbLogRepository).save(argThat(log -> !log.flashed()));
        }

        @Test
        @DisplayName("not flashed: not topped")
        void createClimbLog_notTopped_notFlashed() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, false, 1, NOW);

            when(climbLogRepository.existsByUserIdAndRouteIdAndClimbedAtBefore("u1", "route-1", NOW))
                    .thenReturn(false);
            ClimbLog saved = makeLog("log-1", "u1", "gym-1", "route-1", null, false, false, 1, NOW);
            when(climbLogRepository.save(any(ClimbLog.class))).thenReturn(saved);

            ClimbLogDto result = climbLogService.createClimbLog("u1", request);

            assertThat(result.flashed()).isFalse();
        }

        @Test
        @DisplayName("sessionId is passed through when provided")
        void createClimbLog_withSessionId_sessionIdPersisted() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", "sess-42", true, 2, NOW);

            when(climbLogRepository.existsByUserIdAndRouteIdAndClimbedAtBefore(any(), any(), any()))
                    .thenReturn(false);
            ClimbLog saved = makeLog("log-1", "u1", "gym-1", "route-1", "sess-42", true, false, 2, NOW);
            when(climbLogRepository.save(any())).thenReturn(saved);

            ClimbLogDto result = climbLogService.createClimbLog("u1", request);

            assertThat(result.sessionId()).isEqualTo("sess-42");
        }
    }

    // -------------------------------------------------------------------------
    // createClimbLog — validation
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("createClimbLog() — validation")
    class CreateClimbLogValidation {

        @Test
        @DisplayName("blank gymId throws IllegalArgumentException")
        void createClimbLog_blankGymId_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "  ", "route-1", null, true, 1, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Gym id");
        }

        @Test
        @DisplayName("null gymId throws IllegalArgumentException")
        void createClimbLog_nullGymId_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    null, "route-1", null, true, 1, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("blank routeId throws IllegalArgumentException")
        void createClimbLog_blankRouteId_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "   ", null, true, 1, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Route id");
        }

        @Test
        @DisplayName("null routeId throws IllegalArgumentException")
        void createClimbLog_nullRouteId_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", null, null, true, 1, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null climbedAt throws IllegalArgumentException")
        void createClimbLog_nullClimbedAt_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 1, null);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("climbedAt");
        }

        @Test
        @DisplayName("attempts=0 throws IllegalArgumentException")
        void createClimbLog_zeroAttempts_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 0, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Attempts");
        }

        @Test
        @DisplayName("attempts=-1 throws IllegalArgumentException")
        void createClimbLog_negativeAttempts_throwsIllegalArgumentException() {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, -1, NOW);

            assertThatThrownBy(() -> climbLogService.createClimbLog("u1", request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // deleteClimbLog
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("deleteClimbLog()")
    class DeleteClimbLog {

        @Test
        @DisplayName("happy path: deletes existing log owned by user")
        void deleteClimbLog_existingLogOwnedByUser_deletes() {
            ClimbLog log = makeLog("log-1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            when(climbLogRepository.findById("log-1")).thenReturn(Optional.of(log));

            climbLogService.deleteClimbLog("u1", "log-1");

            verify(climbLogRepository).delete(log);
        }

        @Test
        @DisplayName("log not found throws RuntimeException")
        void deleteClimbLog_logNotFound_throwsRuntimeException() {
            when(climbLogRepository.findById("missing-id")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> climbLogService.deleteClimbLog("u1", "missing-id"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Climb log not found");
        }

        @Test
        @DisplayName("userId mismatch throws RuntimeException")
        void deleteClimbLog_userIdMismatch_throwsRuntimeException() {
            ClimbLog log = makeLog("log-1", "u1", "gym-1", "route-1", null, true, false, 1, NOW);
            when(climbLogRepository.findById("log-1")).thenReturn(Optional.of(log));

            assertThatThrownBy(() -> climbLogService.deleteClimbLog("other-user", "log-1"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Climb log not found");

            verify(climbLogRepository, never()).delete(any());
        }
    }
}
