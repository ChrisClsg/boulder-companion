package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.Gym;
import de.clsg.boulder_companion.repository.GymRepository;
import de.clsg.boulder_companion.dto.GymDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class GymService {

    private final GymRepository gymRepository;

    public GymService(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Transactional(readOnly = true)
    public List<GymDto> getAllGyms() {
        return gymRepository.findAll().stream()
            .map(GymDto::fromGym)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<GymDto> getGymById(String id) {
        return gymRepository.findById(id).map(GymDto::fromGym);
    }

    @Transactional
    public GymDto createGym(Gym gym) {
        return GymDto.fromGym(gymRepository.save(gym));
    }

    @Transactional
    public GymDto updateGym(String id, Gym updatedGym) {
        Gym existingGym = gymRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gym not found"));

        existingGym = new Gym(
            existingGym.id(),
            updatedGym.name(),
            updatedGym.address(),
            updatedGym.description(),
            updatedGym.phone(),
            updatedGym.website(),
            updatedGym.openingHours(),
            existingGym.adminId(),
            existingGym.createdAt(),
            Instant.now()
        );

        return GymDto.fromGym(gymRepository.save(existingGym));
    }

    @Transactional
    public void deleteGym(String id) {
        gymRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GymDto> getGymsByAdminId(String adminId) {
        return gymRepository.findByAdminId(adminId).stream()
            .map(GymDto::fromGym)
            .toList();
    }
}
