package de.clsg.boulder_companion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("gyms")
public record Gym(
    @Id String id,
    String name,
    String address,
    String description,
    String phone,
    String website,
    OpeningHours openingHours,
    @Indexed String adminId,
    Instant createdAt,
    Instant updatedAt
) {
    public record OpeningHours(
        String monday,
        String tuesday,
        String wednesday,
        String thursday,
        String friday,
        String saturday,
        String sunday
    ) {}
}
