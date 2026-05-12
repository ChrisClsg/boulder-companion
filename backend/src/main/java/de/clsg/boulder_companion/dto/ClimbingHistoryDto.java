package de.clsg.boulder_companion.dto;

import de.clsg.boulder_companion.model.ClimbingHistory;

import java.time.Instant;

public record ClimbingHistoryDto(
    String id,
    String userId,
    String gymId,
    String routeId,
    boolean topped,
    int tries,
    int userRating,
    ClimbingHistory.DifficultyFeedback difficultyFeedback,
    Instant createdAt
) {
    public static ClimbingHistoryDto fromHistory(ClimbingHistory history) {
        return new ClimbingHistoryDto(
            history.id(),
            history.userId(),
            history.gymId(),
            history.routeId(),
            history.topped(),
            history.tries(),
            history.userRating(),
            history.difficultyFeedback(),
            history.createdAt()
        );
    }

    public ClimbingHistory toHistory() {
        return new ClimbingHistory(id, userId, gymId, routeId, topped, tries,
            userRating != 0 ? userRating : 3, difficultyFeedback, createdAt);
    }
}
