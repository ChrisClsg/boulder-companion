package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;
import java.util.List;

public record Route(
    @Id String id,
    @Indexed String gymId,
    String name,
    Difficulty difficulty,
    String holdColor,
    List<String> holdTypes,
    @Indexed String setterId,
    String wall,
    boolean archived,
    Instant archivedAt,
    List<Image> images,
    @CreatedDate Instant createdAt,
    @LastModifiedDate Instant updatedAt
) {
    public record Difficulty(
        String value,
        String scale
    ) {}

    public record Image(
        String url,
        String caption
    ) {}
}
