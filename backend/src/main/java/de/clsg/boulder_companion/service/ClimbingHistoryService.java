package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.model.ClimbingHistory;
import de.clsg.boulder_companion.repository.ClimbingHistoryRepository;
import de.clsg.boulder_companion.dto.ClimbingHistoryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClimbingHistoryService {

    private final ClimbingHistoryRepository climbingHistoryRepository;

    public ClimbingHistoryService(ClimbingHistoryRepository climbingHistoryRepository) {
        this.climbingHistoryRepository = climbingHistoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ClimbingHistoryDto> getHistoryByUserId(String userId) {
        return climbingHistoryRepository.findByUserId(userId).stream()
            .map(ClimbingHistoryDto::fromHistory)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ClimbingHistoryDto> getHistoryById(String id) {
        return climbingHistoryRepository.findById(id).map(ClimbingHistoryDto::fromHistory);
    }

    @Transactional
    public ClimbingHistoryDto createHistory(ClimbingHistory history) {
        return ClimbingHistoryDto.fromHistory(climbingHistoryRepository.save(history));
    }

    @Transactional
    public ClimbingHistoryDto updateHistory(String id, ClimbingHistory updatedHistory) {
        ClimbingHistory existingHistory = climbingHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Climbing history not found"));

        existingHistory = new ClimbingHistory(
            existingHistory.id(),
            updatedHistory.userId(),
            updatedHistory.gymId(),
            updatedHistory.routeId(),
            updatedHistory.topped(),
            updatedHistory.tries(),
            updatedHistory.userRating(),
            updatedHistory.difficultyFeedback(),
            existingHistory.createdAt()
        );

        return ClimbingHistoryDto.fromHistory(climbingHistoryRepository.save(existingHistory));
    }

    @Transactional
    public void deleteHistory(String id) {
        climbingHistoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ClimbingHistoryDto> getToppedRoutesByUserId(String userId) {
        return climbingHistoryRepository.findByUserId(userId).stream()
            .filter(ClimbingHistory::topped)
            .map(ClimbingHistoryDto::fromHistory)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ClimbingHistoryDto> getHistoryByGymId(String gymId) {
        return climbingHistoryRepository.findByGymId(gymId).stream()
            .map(ClimbingHistoryDto::fromHistory)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ClimbingHistoryDto> getHistoryByRouteId(String routeId) {
        return climbingHistoryRepository.findByRouteId(routeId).stream()
            .map(ClimbingHistoryDto::fromHistory)
            .toList();
    }
}
