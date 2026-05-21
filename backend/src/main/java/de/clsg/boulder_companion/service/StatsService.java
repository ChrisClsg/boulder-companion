package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.stats.ClimbStatsSummaryDto;
import de.clsg.boulder_companion.model.ClimbLog;
import de.clsg.boulder_companion.repository.ClimbLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final ClimbLogRepository climbLogRepository;

    public StatsService(ClimbLogRepository climbLogRepository) {
        this.climbLogRepository = climbLogRepository;
    }

    @Transactional(readOnly = true)
    public ClimbStatsSummaryDto getSummary(String userId) {
        List<ClimbLog> logs = climbLogRepository.findByUserId(userId);

        Map<String, List<ClimbLog>> byRoute = logs.stream()
                .collect(Collectors.groupingBy(ClimbLog::routeId));

        int attemptedCount = byRoute.size();
        int toppedCount = 0;
        int flashedCount = 0;
        int totalAttemptsOnToppedRoutes = 0;

        for (List<ClimbLog> routeLogs : byRoute.values()) {
            boolean wasTopped = routeLogs.stream().anyMatch(ClimbLog::topped);
            boolean wasFlashed = routeLogs.stream().anyMatch(ClimbLog::flashed);
            int attemptsSum = routeLogs.stream().mapToInt(ClimbLog::attempts).sum();

            if (wasTopped) {
                toppedCount++;
                totalAttemptsOnToppedRoutes += attemptsSum;
            }
            if (wasFlashed) {
                flashedCount++;
            }
        }

        double toppedPercentage = attemptedCount > 0
                ? round1((double) toppedCount / attemptedCount * 100)
                : 0;
        double flashedPercentage = toppedCount > 0
                ? round1((double) flashedCount / toppedCount * 100)
                : 0;
        double averageAttemptsPerTop = toppedCount > 0
                ? round1((double) totalAttemptsOnToppedRoutes / toppedCount)
                : 0;

        return new ClimbStatsSummaryDto(
                toppedCount,
                toppedPercentage,
                flashedPercentage,
                averageAttemptsPerTop
        );
    }

    private static double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
