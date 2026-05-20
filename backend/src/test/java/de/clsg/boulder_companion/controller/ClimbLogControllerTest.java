package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.climblog.ClimbLogDto;
import de.clsg.boulder_companion.dto.climblog.CreateClimbLogRequest;
import de.clsg.boulder_companion.service.ClimbLogService;
import de.clsg.boulder_companion.service.CurrentUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for ClimbLogController.
 * ClimbLogService and CurrentUserService are mocked.
 */
@WebMvcTest(ClimbLogController.class)
@EnableConfigurationProperties(AppProperties.class)
class ClimbLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClimbLogService climbLogService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Instant NOW = Instant.parse("2024-06-01T10:00:00Z");
    private static final String USER_ID = "user-123";

    private ClimbLogDto makeDto(String id, boolean topped, boolean flashed, int attempts) {
        return new ClimbLogDto(id, USER_ID, "gym-1", "route-1", null, topped, flashed, attempts, NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // GET /api/climb-logs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/climb-logs")
    class GetClimbLogs {

        @Test
        @DisplayName("returns 200 with climb log list for authenticated user")
        @WithMockUser
        void getClimbLogs_authenticated_returns200WithList() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            ClimbLogDto dto = makeDto("log-1", true, false, 3);
            when(climbLogService.getClimbLogs(eq(USER_ID), any(), any(), any(), any(), any(), any()))
                    .thenReturn(List.of(dto));

            mockMvc.perform(get("/api/climb-logs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value("log-1"))
                    .andExpect(jsonPath("$[0].topped").value(true))
                    .andExpect(jsonPath("$[0].attempts").value(3));
        }

        @Test
        @DisplayName("returns 200 with empty list when no logs exist")
        @WithMockUser
        void getClimbLogs_noLogs_returns200EmptyList() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(climbLogService.getClimbLogs(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/climb-logs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("passes query params to service correctly")
        @WithMockUser
        void getClimbLogs_withQueryParams_passesThemToService() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(climbLogService.getClimbLogs(eq(USER_ID), eq("gym-1"), eq("route-1"),
                    isNull(), eq(true), isNull(), isNull()))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/climb-logs")
                            .param("gymId", "gym-1")
                            .param("routeId", "route-1")
                            .param("topped", "true"))
                    .andExpect(status().isOk());

            verify(climbLogService).getClimbLogs(
                    eq(USER_ID), eq("gym-1"), eq("route-1"), isNull(), eq(true), isNull(), isNull());
        }

        @Test
        @DisplayName("returns 5xx when CurrentUserService throws (no auth principal)")
        @WithMockUser
        void getClimbLogs_currentUserServiceThrows_returns5xx() throws Exception {
            when(currentUserService.getCurrentUserId())
                    .thenThrow(new IllegalStateException("User is not authenticated"));

            mockMvc.perform(get("/api/climb-logs"))
                    .andExpect(status().is5xxServerError());
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/climb-logs
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/climb-logs")
    class CreateClimbLog {

        @Test
        @DisplayName("returns 200 with created log on valid request")
        @WithMockUser
        void createClimbLog_validRequest_returns200WithDto() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            ClimbLogDto saved = makeDto("log-new", true, true, 1);
            when(climbLogService.createClimbLog(eq(USER_ID), any(CreateClimbLogRequest.class)))
                    .thenReturn(saved);

            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 1, NOW);

            mockMvc.perform(post("/api/climb-logs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("log-new"))
                    .andExpect(jsonPath("$.flashed").value(true));
        }

        @Test
        @DisplayName("returns 400 when service throws IllegalArgumentException")
        @WithMockUser
        void createClimbLog_invalidRequest_returns400() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(climbLogService.createClimbLog(eq(USER_ID), any()))
                    .thenThrow(new IllegalArgumentException("Gym id is required"));

            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "", "route-1", null, true, 1, NOW);

            mockMvc.perform(post("/api/climb-logs")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("POST without CSRF token returns 403")
        @WithMockUser
        void createClimbLog_missingCsrf_returns403() throws Exception {
            CreateClimbLogRequest request = new CreateClimbLogRequest(
                    "gym-1", "route-1", null, true, 1, NOW);

            mockMvc.perform(post("/api/climb-logs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/climb-logs/{id}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("DELETE /api/climb-logs/{id}")
    class DeleteClimbLog {

        @Test
        @DisplayName("returns 204 on successful deletion")
        @WithMockUser
        void deleteClimbLog_existingLog_returns204() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            doNothing().when(climbLogService).deleteClimbLog(USER_ID, "log-1");

            mockMvc.perform(delete("/api/climb-logs/log-1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 5xx when log not found")
        @WithMockUser
        void deleteClimbLog_notFound_returns5xx() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            doThrow(new RuntimeException("Climb log not found"))
                    .when(climbLogService).deleteClimbLog(USER_ID, "missing");

            mockMvc.perform(delete("/api/climb-logs/missing")
                            .with(csrf()))
                    .andExpect(status().is5xxServerError());
        }

        @Test
        @DisplayName("DELETE without CSRF returns 403")
        @WithMockUser
        void deleteClimbLog_missingCsrf_returns403() throws Exception {
            mockMvc.perform(delete("/api/climb-logs/log-1"))
                    .andExpect(status().isForbidden());
        }
    }
}
