package de.clsg.boulder_companion.model;

import lombok.Builder;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("users")
@Builder
public record User(
    @Id String id,
    @Indexed(unique = true) String githubId,
    String name,
    String email,
    Role role,
    List<String> favoriteGyms,
    List<String> favoriteRoutes,
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
        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            safeList(favoriteRoutes),
            safeList(gymAdminFor),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User addFavoriteGym(String gymId) {
        if (gymId == null || gymId.isBlank() || contains(favoriteGyms, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            append(favoriteGyms, gymId),
            safeList(favoriteRoutes),
            safeList(gymAdminFor),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User removeFavoriteGym(String gymId) {
        if (gymId == null || gymId.isBlank() || !contains(favoriteGyms, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            remove(favoriteGyms, gymId),
            safeList(favoriteRoutes),
            safeList(gymAdminFor),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User addFavoriteRoute(String routeId) {
        if (routeId == null || routeId.isBlank() || contains(favoriteRoutes, routeId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            append(favoriteRoutes, routeId),
            safeList(gymAdminFor),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User removeFavoriteRoute(String routeId) {
        if (routeId == null || routeId.isBlank() || !contains(favoriteRoutes, routeId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            remove(favoriteRoutes, routeId),
            safeList(gymAdminFor),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User addGymAdminFor(String gymId) {
        if (gymId == null || gymId.isBlank() || contains(gymAdminFor, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            safeList(favoriteRoutes),
            append(gymAdminFor, gymId),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User removeGymAdminFor(String gymId) {
        if (gymId == null || gymId.isBlank() || !contains(gymAdminFor, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            safeList(favoriteRoutes),
            remove(gymAdminFor, gymId),
            safeList(routeSetterFor),
            createdAt
        );
    }

    public User addRouteSetterFor(String gymId) {
        if (gymId == null || gymId.isBlank() || contains(routeSetterFor, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            safeList(favoriteRoutes),
            safeList(gymAdminFor),
            append(routeSetterFor, gymId),
            createdAt
        );
    }

    public User removeRouteSetterFor(String gymId) {
        if (gymId == null || gymId.isBlank() || !contains(routeSetterFor, gymId)) {
            return this;
        }

        return new User(
            id,
            githubId,
            name,
            email,
            role,
            safeList(favoriteGyms),
            safeList(favoriteRoutes),
            safeList(gymAdminFor),
            remove(routeSetterFor, gymId),
            createdAt
        );
    }

    private static boolean contains(List<String> list, String value) {
        return list != null && list.contains(value);
    }

    private static List<String> append(List<String> list, String value) {
        List<String> result = new ArrayList<>(safeList(list));
        result.add(value);
        return List.copyOf(result);
    }

    private static List<String> remove(List<String> list, String value) {
        return safeList(list).stream()
            .filter(item -> !item.equals(value))
            .toList();
    }

    private static List<String> safeList(List<String> list) {
        return list == null ? List.of() : List.copyOf(list);
    }
}
