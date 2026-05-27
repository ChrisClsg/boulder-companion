package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.dto.UserDto;
import de.clsg.boulder_companion.model.User;
import de.clsg.boulder_companion.repository.GymRepository;
import de.clsg.boulder_companion.repository.UserRepository;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final GymRepository gymRepository;
  private final AuthService authService;

  public UserService(
      UserRepository userRepository,
      GymRepository gymRepository,
      AuthService authService
  ) {
    this.userRepository = userRepository;
    this.gymRepository = gymRepository;
    this.authService = authService;
  }

  public List<GymDto> getFavoriteGyms(OAuth2User principal) {
      User user = authService.getOrCreateUser(principal);

      List<String> favoriteGymIds = user.favoriteGyms() == null
          ? List.of()
          : user.favoriteGyms();

      return gymRepository.findAllById(favoriteGymIds).stream()
          .map(GymDto::fromGym)
          .toList();
  }

  public UserDto addFavoriteGym(OAuth2User principal, String gymId) {
    User user = authService.getOrCreateUser(principal);

    if (!gymRepository.existsById(gymId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Gym not found with id: " + gymId
      );
    }

    User updatedUser = user.addFavoriteGym(gymId);
    User savedUser = userRepository.save(updatedUser);

    return UserDto.fromUser(savedUser);
  }

  public UserDto removeFavoriteGym(OAuth2User principal, String gymId) {
    User user = authService.getOrCreateUser(principal);

    User updatedUser = user.removeFavoriteGym(gymId);
    User savedUser = userRepository.save(updatedUser);

    return UserDto.fromUser(savedUser);
  }
}
