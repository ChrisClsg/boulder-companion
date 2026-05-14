package de.clsg.boulder_companion.security;

import de.clsg.boulder_companion.service.AuthService;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @GetMapping("/csrf")
  public CsrfToken csrf(CsrfToken token) {
    return token;
  }

  @GetMapping("/me")
  public ResponseEntity<String> getMe(@AuthenticationPrincipal OAuth2User user) {
    return ResponseEntity.ok(user.getAttribute("login").toString());
  }

  @GetMapping("/github/callback")
  public ResponseEntity<UserDto> authenticateGitHub(OAuth2User user) {
    UserDto userDto = authService.authenticateUser(user);
    return ResponseEntity.ok(userDto);
  }

  @GetMapping("/oauth2-user")
  public ResponseEntity<java.util.Map<String, Object>> getOAuth2User() {
    java.util.Map<String, Object> oauth2User = authService.getOAuth2User();
    return ResponseEntity.ok(oauth2User);
  }

  @GetMapping("/user")
  public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
    return ResponseEntity.ok(authService.getCurrentUser());
  }

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }
}
