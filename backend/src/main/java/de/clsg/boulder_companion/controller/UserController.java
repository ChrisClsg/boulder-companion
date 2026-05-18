package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
    UserDto user = userService.getCurrentUser(principal);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/me/favorite-gyms")
  public ResponseEntity<List<GymDto>> getFavoriteGyms(
      @AuthenticationPrincipal OAuth2User principal
  ) {
      return ResponseEntity.ok(userService.getFavoriteGyms(principal));
  }

  @PutMapping("/me/favorite-gyms/{gymId}")
  public ResponseEntity<UserDto> addFavoriteGym(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable String gymId
  ) {
    UserDto user = userService.addFavoriteGym(principal, gymId);
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/me/favorite-gyms/{gymId}")
  public ResponseEntity<UserDto> removeFavoriteGym(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable String gymId
  ) {
    UserDto user = userService.removeFavoriteGym(principal, gymId);
    return ResponseEntity.ok(user);
  }
}
