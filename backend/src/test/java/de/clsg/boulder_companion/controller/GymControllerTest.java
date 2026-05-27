package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.service.GymService;
import de.clsg.boulder_companion.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice tests for GymController.
 * GymService is mocked; full Spring MVC pipeline is exercised.
 */
@WebMvcTest(GymController.class)
@EnableConfigurationProperties(AppProperties.class)
@Import(SecurityConfig.class)
class GymControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymService gymService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private GymDto makeGymDto(String id, String name) {
        return new GymDto(id, name, "Address", "Description", "0800",
                "https://example.com", null, "admin-1", NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // GET /api/gyms
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/gyms")
    class GetAllGyms {

        @Test
        @DisplayName("returns 200 with empty array when no gyms exist")
        @WithMockUser
        void getAllGyms_noGyms_returns200EmptyArray() throws Exception {
            when(gymService.getAllGyms()).thenReturn(List.of());

            mockMvc.perform(get("/api/gyms"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("returns 200 with gym list")
        @WithMockUser
        void getAllGyms_multipleGyms_returns200WithList() throws Exception {
            GymDto g1 = makeGymDto("g1", "Boulderhaus");
            GymDto g2 = makeGymDto("g2", "Kletterhalle");
            when(gymService.getAllGyms()).thenReturn(List.of(g1, g2));

            mockMvc.perform(get("/api/gyms"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("g1"))
                    .andExpect(jsonPath("$[0].name").value("Boulderhaus"))
                    .andExpect(jsonPath("$[1].id").value("g2"))
                    .andExpect(jsonPath("$[1].name").value("Kletterhalle"));
        }

        @Test
        @DisplayName("returns 200 without authentication (public endpoint)")
        void getAllGyms_unauthenticated_returns200() throws Exception {
            when(gymService.getAllGyms()).thenReturn(List.of());

            mockMvc.perform(get("/api/gyms").with(anonymous())
                            .cookie(new Cookie("XSRF-TOKEN", "test-token")))
                    .andExpect(status().isOk());
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/gyms/{id}
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/gyms/{id}")
    class GetGymById {

        @Test
        @DisplayName("returns 200 with gym when found")
        @WithMockUser
        void getGymById_existingGym_returns200WithGym() throws Exception {
            GymDto dto = makeGymDto("g1", "Boulderhaus");
            when(gymService.getGymById("g1")).thenReturn(Optional.of(dto));

            mockMvc.perform(get("/api/gyms/g1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("g1"))
                    .andExpect(jsonPath("$.name").value("Boulderhaus"));
        }

        @Test
        @DisplayName("returns 404 when gym not found")
        @WithMockUser
        void getGymById_unknownGym_returns404() throws Exception {
            when(gymService.getGymById("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/gyms/unknown"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 200 without authentication (public endpoint)")
        void getGymById_unauthenticated_returns200() throws Exception {
            GymDto dto = makeGymDto("g1", "Boulderhaus");
            when(gymService.getGymById("g1")).thenReturn(Optional.of(dto));

            mockMvc.perform(get("/api/gyms/g1").with(anonymous())
                            .cookie(new Cookie("XSRF-TOKEN", "test-token")))
                    .andExpect(status().isOk());
        }
    }
}
