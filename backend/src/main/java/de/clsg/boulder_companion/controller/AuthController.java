package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.AuthService;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/github/callback")
    public ResponseEntity<UserDto> authenticateGitHub(OAuth2User user) {
        UserDto userDto = authService.authenticateUser(user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @GetMapping("/oauth2-user")
    public ResponseEntity<Map<String, Object>> getOAuth2User() {
        Map<String, Object> oauth2User = authService.getOAuth2User();
        return ResponseEntity.ok(oauth2User);
    }
}
