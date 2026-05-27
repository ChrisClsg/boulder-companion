package de.clsg.boulder_companion.dto;

import de.clsg.boulder_companion.model.User;

import java.util.List;

public record UserDto(
    String id,
    String githubId,
    String name,
    String email,
    User.Role role,
    List<String> favoriteGyms,
    List<String> gymAdminFor,
    List<String> routeSetterFor
) {

  public static UserDto fromUser(User user) {
    return new UserDto(
        user.id(),
        user.githubId(),
        user.name(),
        user.email(),
        user.role(),
        safeList(user.favoriteGyms()),
        safeList(user.gymAdminFor()),
        safeList(user.routeSetterFor())
    );
  }

  private static List<String> safeList(List<String> list) {
    return list == null ? List.of() : List.copyOf(list);
  }
}
