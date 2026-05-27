package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.oauth2.core.user.OAuth2UserAuthority.*;

/**
 * Unit tests for AuthService.
 * UserRepository is mocked; OAuth2User is constructed via simple maps.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    /** Creates a minimal OAuth2User backed by a simple attribute map. */
    private OAuth2User mockOAuth2User(Map<String, Object> attributes) {
        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                List.of(new org.springframework.security.oauth2.core.user.OAuth2UserAuthority(attributes)),
                attributes,
                "login"
        );
    }

    private User savedUser(String id, String githubId, String name, String email) {
        return new User(id, githubId, name, email, User.Role.CLIMBER, List.of(), List.of(), List.of(), NOW);
    }

    // -------------------------------------------------------------------------
    // getOrCreateUser
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getOrCreateUser()")
    class GetOrCreateUser {

        @Test
        @DisplayName("null principal throws ResponseStatusException with UNAUTHORIZED status")
        void getOrCreateUser_nullPrincipal_throwsUnauthorized() {
            assertThatThrownBy(() -> authService.getOrCreateUser(null))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Not authenticated");
        }

        @Test
        @DisplayName("existing user is returned from repository without creating a new one")
        void getOrCreateUser_existingUser_returnsExistingUser() {
            User existing = savedUser("u-1", "42", "Alice", "alice@example.com");
            OAuth2User principal = mockOAuth2User(Map.of("id", 42, "login", "alice", "name", "Alice"));

            when(userRepository.findByGithubId("42")).thenReturn(Optional.of(existing));

            User result = authService.getOrCreateUser(principal);

            assertThat(result.id()).isEqualTo("u-1");
            assertThat(result.githubId()).isEqualTo("42");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("new user is created with CLIMBER role when not found in repository")
        void getOrCreateUser_newUser_createsWithClimberRole() {
            OAuth2User principal = mockOAuth2User(Map.of(
                    "id", 99, "login", "newuser", "name", "New User", "email", "new@example.com"));

            when(userRepository.findByGithubId("99")).thenReturn(Optional.empty());

            User savedUser = new User(null, "99", "New User", "new@example.com",
                    User.Role.CLIMBER, List.of(), List.of(), List.of(), null);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            User result = authService.getOrCreateUser(principal);

            assertThat(result.role()).isEqualTo(User.Role.CLIMBER);
            assertThat(result.name()).isEqualTo("New User");
            assertThat(result.email()).isEqualTo("new@example.com");
            verify(userRepository).save(any());
        }

        @Test
        @DisplayName("falls back to login as name when name attribute is blank")
        void getOrCreateUser_blankName_usesLoginAsName() {
            OAuth2User principal = mockOAuth2User(Map.of(
                    "id", 77, "login", "githubuser", "name", ""));

            when(userRepository.findByGithubId("77")).thenReturn(Optional.empty());

            User newUser = new User(null, "77", "githubuser", null,
                    User.Role.CLIMBER, List.of(), List.of(), List.of(), null);
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            User result = authService.getOrCreateUser(principal);

            assertThat(result.name()).isEqualTo("githubuser");
        }

        @Test
        @DisplayName("falls back to login as name when name attribute is absent")
        void getOrCreateUser_noNameAttribute_usesLoginAsName() {
            OAuth2User principal = mockOAuth2User(Map.of(
                    "id", 55, "login", "coderX"));

            when(userRepository.findByGithubId("55")).thenReturn(Optional.empty());

            User newUser = new User(null, "55", "coderX", null,
                    User.Role.CLIMBER, List.of(), List.of(), List.of(), null);
            when(userRepository.save(argThat(u -> "coderX".equals(u.name())))).thenReturn(newUser);

            User result = authService.getOrCreateUser(principal);

            assertThat(result.name()).isEqualTo("coderX");
        }

        @Test
        @DisplayName("principal missing 'id' attribute throws ResponseStatusException")
        void getOrCreateUser_missingIdAttribute_throwsUnauthorized() {
            OAuth2User principal = mockOAuth2User(Map.of("login", "alice"));

            assertThatThrownBy(() -> authService.getOrCreateUser(principal))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Missing OAuth2 attribute: id");
        }

        @Test
        @DisplayName("githubId is stored as string representation of the id attribute")
        void getOrCreateUser_integerIdAttribute_storedAsString() {
            OAuth2User principal = mockOAuth2User(Map.of(
                    "id", 12345, "login", "alice", "name", "Alice"));

            when(userRepository.findByGithubId("12345")).thenReturn(Optional.empty());

            User newUser = savedUser(null, "12345", "Alice", null);
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            authService.getOrCreateUser(principal);

            verify(userRepository).findByGithubId("12345");
        }
    }
}
