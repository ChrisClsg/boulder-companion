package de.clsg.boulder_companion.security;

import de.clsg.boulder_companion.config.AppProperties;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.security.SecurityConfig;
import de.clsg.boulder_companion.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@EnableConfigurationProperties(AppProperties.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // -------------------------------------------------------------------------
    // GET /api/auth/csrf
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/auth/csrf")
    class GetCsrfToken {

        @Test
        @DisplayName("returns 200 with CSRF token when unauthenticated (public endpoint)")
        void getCsrfToken_unauthenticated_returns200WithToken() throws Exception {
            mockMvc.perform(get("/api/auth/csrf").with(anonymous())
                            .cookie(new Cookie("XSRF-TOKEN", "test-token")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.headerName").exists())
                    .andExpect(jsonPath("$.parameterName").exists());
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/auth/me
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetCurrentUser {

        @Test
        @DisplayName("returns 200 with user DTO when authenticated via OAuth2")
        void getCurrentUser_authenticated_returns200WithDto() throws Exception {
            UserDto dto = new UserDto("u-1", "42", "Alice", "alice@example.com",
                    User.Role.CLIMBER, List.of(), List.of(), List.of());
            when(authService.getOrCreateCurrentUser(any())).thenReturn(dto);

            mockMvc.perform(get("/api/auth/me")
                            .with(oidcLogin().userInfoToken(token -> token
                                    .claim("id", "42")
                                    .claim("login", "alice")
                                    .claim("name", "Alice"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u-1"))
                    .andExpect(jsonPath("$.name").value("Alice"))
                    .andExpect(jsonPath("$.githubId").value("42"));
        }

        @Test
        @DisplayName("returns 3xx redirect when unauthenticated")
        void getCurrentUser_unauthenticated_returnsRedirect() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().is3xxRedirection());
        }
    }
}
