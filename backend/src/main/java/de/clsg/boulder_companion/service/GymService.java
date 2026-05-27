package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.repository.GymRepository;
import de.clsg.boulder_companion.dto.GymDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
