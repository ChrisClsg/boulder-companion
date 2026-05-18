package de.clsg.boulder_companion.dto.climblog;

import java.time.Instant;

public record CreateClimbLogRequest(
    String gymId,
    String routeId,
    String sessionId,
    boolean topped,
    boolean flashed,
    int attempts,
    Instant climbedAt
) {}
