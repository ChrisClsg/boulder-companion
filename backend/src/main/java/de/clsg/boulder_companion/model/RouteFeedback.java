package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("route_feedbacks")
@CompoundIndex(
    name = "unique_user_route_feedback",
    def = "{'userId': 1, 'routeId': 1}",
    unique = true
)
public record RouteFeedback(
    @Id String id,

    @Indexed String userId,
    @Indexed String routeId,
    @Indexed String gymId,

    int userRating,
    DifficultyFeedback difficultyFeedback,

    @CreatedDate Instant createdAt,
    @LastModifiedDate Instant updatedAt
) {
    public enum DifficultyFeedback {
        MUCH_HARDER,
        HARDER,
        ABOUT_RIGHT,
        EASIER,
        MUCH_EASIER
    }
}
