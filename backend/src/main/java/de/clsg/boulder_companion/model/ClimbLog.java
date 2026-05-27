package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("climb_logs")
public record ClimbLog(
    @Id String id,

    @Indexed String userId,
    @Indexed String gymId,
    @Indexed String routeId,

    @Indexed String sessionId,

    boolean topped,
    boolean flashed,
    int attempts,

    // actual climbing date/time
    @Indexed Instant climbedAt,

    // database creation time
    @CreatedDate Instant createdAt
) {}
