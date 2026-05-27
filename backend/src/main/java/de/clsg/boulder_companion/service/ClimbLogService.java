package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.climblog.ClimbLogDto;
import de.clsg.boulder_companion.dto.climblog.CreateClimbLogRequest;
import de.clsg.boulder_companion.model.ClimbLog;
import de.clsg.boulder_companion.repository.ClimbLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class ClimbLogService {

    private final ClimbLogRepository climbLogRepository;

    public ClimbLogService(ClimbLogRepository climbLogRepository) {
        this.climbLogRepository = climbLogRepository;
    }

    @Transactional(readOnly = true)
    public List<ClimbLogDto> getClimbLogs(
            String userId,
            String gymId,
            String routeId,
            String sessionId,
            Boolean topped,
            Instant from,
            Instant to
    ) {
        return climbLogRepository.findByUserId(userId).stream()
                .filter(log -> gymId == null || gymId.equals(log.gymId()))
                .filter(log -> routeId == null || routeId.equals(log.routeId()))
                .filter(log -> sessionId == null || sessionId.equals(log.sessionId()))
                .filter(log -> topped == null || topped == log.topped())
                .filter(log -> from == null || !log.climbedAt().isBefore(from))
                .filter(log -> to == null || log.climbedAt().isBefore(to))
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ClimbLogDto createClimbLog(
            String userId,
            CreateClimbLogRequest request
    ) {
        validateCreateRequest(request);

        boolean previousLogsExist = climbLogRepository
                .existsByUserIdAndRouteIdAndClimbedAtBefore(userId, request.routeId(), request.climbedAt());

        boolean flashed = request.topped() && request.attempts() == 1 && !previousLogsExist;

        ClimbLog climbLog = new ClimbLog(
                null,
                userId,
                request.gymId(),
                request.routeId(),
                request.sessionId(),
                request.topped(),
                flashed,
                request.attempts(),
                request.climbedAt(),
                null
        );

        return toDto(climbLogRepository.save(climbLog));
    }

    @Transactional
    public void deleteClimbLog(String userId, String id) {
        ClimbLog existingLog = climbLogRepository.findById(id)
                .filter(log -> log.userId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Climb log not found"));

        climbLogRepository.delete(existingLog);
    }

    private void validateCreateRequest(CreateClimbLogRequest request) {
        if (request.gymId() == null || request.gymId().isBlank()) {
            throw new IllegalArgumentException("Gym id is required");
        }

        if (request.routeId() == null || request.routeId().isBlank()) {
            throw new IllegalArgumentException("Route id is required");
        }

        if (request.climbedAt() == null) {
            throw new IllegalArgumentException("climbedAt is required");
        }

        validateAttempts(request.attempts());
    }

    private void validateAttempts(int attempts) {
        if (attempts < 1) {
            throw new IllegalArgumentException("Attempts must be at least 1");
        }
    }

    private ClimbLogDto toDto(ClimbLog climbLog) {
        return new ClimbLogDto(
                climbLog.id(),
                climbLog.userId(),
                climbLog.gymId(),
                climbLog.routeId(),
                climbLog.sessionId(),
                climbLog.topped(),
                climbLog.flashed(),
                climbLog.attempts(),
                climbLog.climbedAt(),
                climbLog.createdAt()
        );
    }
}
