package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.feedback.RouteFeedbackDto;
import de.clsg.boulder_companion.dto.feedback.UpsertRouteFeedbackRequest;
import de.clsg.boulder_companion.model.RouteFeedback.DifficultyFeedback;
import de.clsg.boulder_companion.service.CurrentUserService;
import de.clsg.boulder_companion.service.RouteFeedbackService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for RouteFeedbackController.
 * RouteFeedbackService and CurrentUserService are mocked.
 */
@WebMvcTest(RouteFeedbackController.class)
@EnableConfigurationProperties(AppProperties.class)
class RouteFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteFeedbackService routeFeedbackService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Instant NOW = Instant.parse("2024-03-15T12:00:00Z");
    private static final String USER_ID = "user-abc";

    private RouteFeedbackDto makeDto(String routeId, int rating, DifficultyFeedback difficulty) {
        return new RouteFeedbackDto("fb-1", USER_ID, routeId, "gym-1", rating, difficulty, NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // GET /api/routes/{routeId}/my-feedback
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/routes/{routeId}/my-feedback")
    class GetMyFeedback {

        @Test
        @DisplayName("returns 200 with feedback body when feedback exists")
        @WithMockUser
        void getMyFeedback_feedbackExists_returns200WithBody() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            RouteFeedbackDto dto = makeDto("route-1", 4, DifficultyFeedback.ABOUT_RIGHT);
            when(routeFeedbackService.getMyFeedback(USER_ID, "route-1"))
                    .thenReturn(Optional.of(dto));

            mockMvc.perform(get("/api/routes/route-1/my-feedback"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("fb-1"))
                    .andExpect(jsonPath("$.userId").value(USER_ID))
                    .andExpect(jsonPath("$.routeId").value("route-1"))
                    .andExpect(jsonPath("$.userRating").value(4))
                    .andExpect(jsonPath("$.difficultyFeedback").value("ABOUT_RIGHT"));
        }

        @Test
        @DisplayName("returns 204 No Content when no feedback exists")
        @WithMockUser
        void getMyFeedback_noFeedback_returns204() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(routeFeedbackService.getMyFeedback(USER_ID, "route-1"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/routes/route-1/my-feedback"))
                    .andExpect(status().isNoContent());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/routes/{routeId}/my-feedback
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PUT /api/routes/{routeId}/my-feedback")
    class UpsertMyFeedback {

        @Test
        @DisplayName("returns 200 with updated feedback on valid request")
        @WithMockUser
        void upsertMyFeedback_validRequest_returns200WithDto() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            RouteFeedbackDto dto = makeDto("route-1", 5, DifficultyFeedback.EASIER);
            when(routeFeedbackService.upsertMyFeedback(eq(USER_ID), eq("route-1"), any()))
                    .thenReturn(dto);

            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(5, DifficultyFeedback.EASIER);

            mockMvc.perform(put("/api/routes/route-1/my-feedback")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userRating").value(5))
                    .andExpect(jsonPath("$.difficultyFeedback").value("EASIER"));
        }

        @Test
        @DisplayName("returns 5xx when route is not found")
        @WithMockUser
        void upsertMyFeedback_routeNotFound_returns5xx() throws Exception {
            when(currentUserService.getCurrentUserId()).thenReturn(USER_ID);
            when(routeFeedbackService.upsertMyFeedback(any(), any(), any()))
                    .thenThrow(new RuntimeException("Route not found"));

            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(3, DifficultyFeedback.HARDER);

            mockMvc.perform(put("/api/routes/missing-route/my-feedback")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is5xxServerError());
        }

        @Test
        @DisplayName("PUT without CSRF returns 403")
        @WithMockUser
        void upsertMyFeedback_missingCsrf_returns403() throws Exception {
            UpsertRouteFeedbackRequest request = new UpsertRouteFeedbackRequest(3, DifficultyFeedback.ABOUT_RIGHT);

            mockMvc.perform(put("/api/routes/route-1/my-feedback")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }
}
