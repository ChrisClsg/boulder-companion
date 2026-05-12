package de.clsg.boulder_companion.dto;

import de.clsg.boulder_companion.model.User;

import java.util.List;
import java.util.stream.Collectors;

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
            user.favoriteGyms(),
            user.gymAdminFor(),
            user.routeSetterFor()
        );
    }

    public User toUser() {
        return new User(id, githubId, name, email, role,
            favoriteGyms != null ? favoriteGyms : List.of(),
            gymAdminFor != null ? gymAdminFor : List.of(),
            routeSetterFor != null ? routeSetterFor : List.of(),
            null);
    }
}
