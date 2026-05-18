package de.clsg.boulder_companion.dto.feedback;

import de.clsg.boulder_companion.model.RouteFeedback.DifficultyFeedback;

public record UpsertRouteFeedbackRequest(
    int userRating,
    DifficultyFeedback difficultyFeedback
) {}
