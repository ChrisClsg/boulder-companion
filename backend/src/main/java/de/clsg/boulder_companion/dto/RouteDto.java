package de.clsg.boulder_companion.dto;

import de.clsg.boulder_companion.model.Route;

import java.time.Instant;
import java.util.List;

public record RouteDto(
    String id,
    String gymId,
    String name,
    Route.Difficulty difficulty,
    String holdColor,
    List<String> holdTypes,
    String setterId,
    String wall,
    boolean archived,
    Instant archivedAt,
    List<Route.Image> images,
    Instant createdAt,
    Instant updatedAt
) {
    public static RouteDto fromRoute(Route route) {
        return new RouteDto(
            route.id(),
            route.gymId(),
            route.name(),
            route.difficulty(),
            route.holdColor(),
            route.holdTypes() != null ? List.copyOf(route.holdTypes()) : List.of(),
            route.setterId(),
            route.wall(),
            route.archived(),
            route.archivedAt(),
            route.images() != null ? List.copyOf(route.images()) : List.of(),
            route.createdAt(),
            route.updatedAt()
        );
    }

    public Route toRoute() {
        return new Route(
            id,
            gymId,
            name,
            difficulty,
            holdColor,
            holdTypes != null ? List.copyOf(holdTypes) : List.of(),
            setterId,
            wall,
            archived,
            archivedAt,
            images != null ? List.copyOf(images) : List.of(),
            createdAt,
            updatedAt
        );
    }
}
