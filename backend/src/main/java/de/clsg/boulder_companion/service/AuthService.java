package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.UserRepository;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class AuthService {

  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto getOrCreateCurrentUser(OAuth2User principal) {
    User user = getOrCreateUser(principal);
    return UserDto.fromUser(user);
  }

  public User getOrCreateUser(OAuth2User principal) {
    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    String githubId = getRequiredAttribute(principal, "id");

    return userRepository.findByGithubId(githubId)
        .orElseGet(() -> createUserFromGithub(principal, githubId));
  }

  private User createUserFromGithub(OAuth2User principal, String githubId) {
    String login = getNullableAttribute(principal, "login");
    String name = getNullableAttribute(principal, "name");
    String email = getNullableAttribute(principal, "email");

    User newUser = new User(
        null,
        githubId,
        name != null && !name.isBlank() ? name : login,
        email,
        User.Role.CLIMBER,
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        null
    );

    return userRepository.save(newUser);
  }

  private String getRequiredAttribute(OAuth2User principal, String attributeName) {
    Object value = principal.getAttribute(attributeName);

    if (value == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "Missing OAuth2 attribute: " + attributeName
      );
    }

    return value.toString();
  }

  private String getNullableAttribute(OAuth2User principal, String attributeName) {
    Object value = principal.getAttribute(attributeName);
    return value != null ? value.toString() : null;
  }
}
