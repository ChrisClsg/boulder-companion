package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.stats.ClimbStatsSummaryDto;
import de.clsg.boulder_companion.service.CurrentUserService;
import de.clsg.boulder_companion.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice tests for StatsController.
 * StatsService and CurrentUserService are mocked.
 */
@WebMvcTest(StatsController.class)
@EnableConfigurationProperties(AppProperties.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatsService statsService;

    @MockitoBean
    private CurrentUserService currentUserService;

    private static final String USER_ID = "user-123";

    // -------------------------------------------------------------------------
    // GET /api/me/stats/summary
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/me/stats/summary")
    class GetSummary {

        @Test
        @DisplayName("returns 200 with summary for authenticated user")
        @WithMockUser
        void getSummary_authenticated_returns200WithSummary() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            ClimbStatsSummaryDto summary = new ClimbStatsSummaryDto(7, 70.0, 42.9, 2.3);
            when(statsService.getSummary(USER_ID)).thenReturn(summary);

            mockMvc.perform(get("/api/me/stats/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.toppedCount").value(7))
                    .andExpect(jsonPath("$.toppedPercentage").value(70.0))
                    .andExpect(jsonPath("$.flashedPercentage").value(42.9))
                    .andExpect(jsonPath("$.averageAttemptsPerTop").value(2.3));
        }

        @Test
        @DisplayName("returns 200 with all-zero summary for user with no logs")
        @WithMockUser
        void getSummary_noLogs_returns200WithZeros() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(statsService.getSummary(USER_ID)).thenReturn(new ClimbStatsSummaryDto(0, 0, 0, 0));

            mockMvc.perform(get("/api/me/stats/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.toppedCount").value(0))
                    .andExpect(jsonPath("$.toppedPercentage").value(0))
                    .andExpect(jsonPath("$.flashedPercentage").value(0))
                    .andExpect(jsonPath("$.averageAttemptsPerTop").value(0));
        }

        @Test
        @DisplayName("returns 5xx when CurrentUserService throws (no auth principal)")
        @WithMockUser
        void getSummary_currentUserServiceThrows_returns5xx() throws Exception {
            when(currentUserService.getCurrentUserId())
                    .thenThrow(new IllegalStateException("User is not authenticated"));

            mockMvc.perform(get("/api/me/stats/summary"))
                    .andExpect(status().is5xxServerError());
        }
    }
}
