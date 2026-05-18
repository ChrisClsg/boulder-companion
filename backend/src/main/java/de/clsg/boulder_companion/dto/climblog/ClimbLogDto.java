package de.clsg.boulder_companion.dto.climblog;

import java.time.Instant;

public record ClimbLogDto(
    String id,
    String userId,
    String gymId,
    String routeId,
    String sessionId,
    boolean topped,
    boolean flashed,
    int attempts,
    Instant climbedAt,
    Instant createdAt
) {}
