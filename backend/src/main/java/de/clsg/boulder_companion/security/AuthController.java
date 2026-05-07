package de.clsg.boulder_companion.security;

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
}
