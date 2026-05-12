package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record User(
    @Id String id,
    @Indexed(unique = true) String githubId,
    String name,
    String email,
    Role role,
    List<String> favoriteGyms,
    List<String> gymAdminFor,
    List<String> routeSetterFor,
    @CreatedDate Instant createdAt
) {
    public enum Role {
        CLIMBER,
        GYM_ADMIN,
        ROUTE_SETTER
    }

    public User withRole(Role role) {
        return new User(id, githubId, name, email, role, favoriteGyms, gymAdminFor, routeSetterFor, createdAt);
    }

    public User addFavoriteGym(String gymId) {
        if (favoriteGyms == null || !favoriteGyms.contains(gymId)) {
            return new User(
                id,
                githubId,
                name,
                email,
                role,
                append(favoriteGyms, gymId),
                gymAdminFor,
                routeSetterFor,
                createdAt
            );
        }
        return this;
    }

    public User removeFavoriteGym(String gymId) {
        if (favoriteGyms != null && favoriteGyms.contains(gymId)) {
            return new User(
                id,
                githubId,
                name,
                email,
                role,
                favoriteGyms.stream()
                    .filter(g -> !g.equals(gymId))
                    .toList(),
                gymAdminFor,
                routeSetterFor,
                createdAt
            );
        }
        return this;
    }

    public User addGymAdminFor(String gymId) {
        if (gymAdminFor == null || !gymAdminFor.contains(gymId)) {
            return new User(
                id,
                githubId,
                name,
                email,
                role,
                favoriteGyms,
                append(gymAdminFor, gymId),
                routeSetterFor,
                createdAt
            );
        }
        return this;
    }

    public User addRouteSetterFor(String gymId) {
        if (routeSetterFor == null || !routeSetterFor.contains(gymId)) {
            return new User(
                id,
                githubId,
                name,
                email,
                role,
                favoriteGyms,
                gymAdminFor,
                append(routeSetterFor, gymId),
                createdAt
            );
        }
        return this;
    }

    private static List<String> append(List<String> list, String value) {
        var result = new java.util.ArrayList<>(list != null ? list : List.<String>of());
        result.add(value);
        return List.copyOf(result);
    }
}
