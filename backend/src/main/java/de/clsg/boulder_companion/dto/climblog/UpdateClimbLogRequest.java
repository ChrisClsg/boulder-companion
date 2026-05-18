package de.clsg.boulder_companion.dto.climblog;

import java.time.Instant;

public record UpdateClimbLogRequest(
    Boolean topped,
    Boolean flashed,
    Integer attempts,
    Instant climbedAt,
    String sessionId
) {}
