package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;
import java.util.List;

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
            return new User(id, githubId, name, email, role,
                List.copyOf(favoriteGyms != null ? favoriteGyms : List.of()) + List.of(gymId),
                gymAdminFor, routeSetterFor, createdAt);
        }
        return this;
    }

    public User removeFavoriteGym(String gymId) {
        if (favoriteGyms != null && favoriteGyms.contains(gymId)) {
            return new User(id, githubId, name, email, role,
                favoriteGyms.stream().filter(g -> !g.equals(gymId)).toList(),
                gymAdminFor, routeSetterFor, createdAt);
        }
        return this;
    }

    public User addGymAdminFor(String gymId) {
        if (gymAdminFor == null || !gymAdminFor.contains(gymId)) {
            return new User(id, githubId, name, email, role, favoriteGyms,
                List.copyOf(gymAdminFor != null ? gymAdminFor : List.of()) + List.of(gymId),
                routeSetterFor, createdAt);
        }
        return this;
    }

    public User addRouteSetterFor(String gymId) {
        if (routeSetterFor == null || !routeSetterFor.contains(gymId)) {
            return new User(id, githubId, name, email, role, favoriteGyms, gymAdminFor,
                List.copyOf(routeSetterFor != null ? routeSetterFor : List.of()) + List.of(gymId),
                createdAt);
        }
        return this;
    }
}
