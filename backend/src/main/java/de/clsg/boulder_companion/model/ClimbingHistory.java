package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;

public record ClimbingHistory(
    @Id String id,
    @Indexed String userId,
    @Indexed String gymId,
    @Indexed String routeId,
    boolean topped,
    int tries,
    int userRating,
    DifficultyFeedback difficultyFeedback,
    @CreatedDate Instant createdAt
) {
    public enum DifficultyFeedback {
        MUCH_HARDER,
        HARDER,
        ABOUT_RIGHT,
        EASIER,
        MUCH_EASIER
    }
}
