package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void setOAuth2SecurityContext(Map<String, Object> attributes) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        DefaultOAuth2User principal = new DefaultOAuth2User(
                List.of(new OAuth2UserAuthority(attributes)),
                attributes,
                "login"
        );
        ctx.setAuthentication(new TestingAuthenticationToken(principal, null, "ROLE_USER"));
        SecurityContextHolder.setContext(ctx);
    }

    private User makeUser(String id, String githubId) {
        return new User(id, githubId, "Alice", "alice@example.com",
                User.Role.CLIMBER, List.of(), List.of(), List.of(), NOW);
    }

    // -------------------------------------------------------------------------
    // getCurrentUserId
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getCurrentUserId()")
    class GetCurrentUserId {

        @Test
        @DisplayName("returns internal user id for authenticated OAuth2 user")
        void getCurrentUserId_validOAuth2User_returnsInternalId() {
            setOAuth2SecurityContext(Map.of("id", 42, "login", "alice"));
            when(userRepository.findByGithubId("42")).thenReturn(Optional.of(makeUser("u-1", "42")));

            String result = currentUserService.getCurrentUserId();

            assertThat(result).isEqualTo("u-1");
        }

        @Test
        @DisplayName("throws IllegalStateException when no authentication is present")
        void getCurrentUserId_noAuthentication_throwsIllegalState() {
            SecurityContextHolder.clearContext();

            assertThatThrownBy(() -> currentUserService.getCurrentUserId())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("User is not authenticated");
        }

        @Test
        @DisplayName("throws IllegalStateException when principal is not an OAuth2User")
        void getCurrentUserId_nonOAuth2Principal_throwsIllegalState() {
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(new UsernamePasswordAuthenticationToken("alice", null, List.of()));
            SecurityContextHolder.setContext(ctx);

            assertThatThrownBy(() -> currentUserService.getCurrentUserId())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Authenticated principal is not an OAuth2 user");
        }

        @Test
        @DisplayName("throws IllegalStateException when OAuth2 user is missing 'id' attribute")
        void getCurrentUserId_missingIdAttribute_throwsIllegalState() {
            setOAuth2SecurityContext(Map.of("login", "alice"));

            assertThatThrownBy(() -> currentUserService.getCurrentUserId())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("OAuth2 user id is missing");
        }

        @Test
        @DisplayName("throws IllegalStateException when user is not in the repository")
        void getCurrentUserId_userNotInRepo_throwsIllegalState() {
            setOAuth2SecurityContext(Map.of("id", 99, "login", "unknown"));
            when(userRepository.findByGithubId("99")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> currentUserService.getCurrentUserId())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Current user not found");
        }
    }
}
