package de.clsg.boulder_companion.dto.feedback;

import de.clsg.boulder_companion.model.RouteFeedback.DifficultyFeedback;

import java.time.Instant;

public record RouteFeedbackDto(
    String id,
    String userId,
    String routeId,
    String gymId,
    int userRating,
    DifficultyFeedback difficultyFeedback,
    Instant createdAt,
    Instant updatedAt
) {}
