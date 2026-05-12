package de.clsg.boulder_companion.dto;

import de.clsg.boulder_companion.model.Gym;

import java.time.Instant;
import java.util.stream.Collectors;

public record GymDto(
    String id,
    String name,
    String address,
    String description,
    String phone,
    String website,
    Gym.OpeningHours openingHours,
    String adminId,
    Instant createdAt,
    Instant updatedAt
) {
    public static GymDto fromGym(Gym gym) {
        return new GymDto(
            gym.id(),
            gym.name(),
            gym.address(),
            gym.description(),
            gym.phone(),
            gym.website(),
            gym.openingHours(),
            gym.adminId(),
            gym.createdAt(),
            gym.updatedAt()
        );
    }

    public Gym toGym() {
        return new Gym(id, name, address, description, phone, website, openingHours, adminId, createdAt, updatedAt);
    }
}
