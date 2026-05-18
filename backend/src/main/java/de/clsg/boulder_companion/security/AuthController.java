package de.clsg.boulder_companion.security;

import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.service.AuthService;

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

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/csrf")
  public CsrfToken csrf(CsrfToken token) {
    return token;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal OAuth2User principal) {
    UserDto user = authService.getOrCreateCurrentUser(principal);
    return ResponseEntity.ok(user);
  }
}
