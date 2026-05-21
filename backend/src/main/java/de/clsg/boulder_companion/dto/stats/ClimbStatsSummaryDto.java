package de.clsg.boulder_companion.dto.stats;

public record ClimbStatsSummaryDto(
        int toppedCount,
        double toppedPercentage,
        double flashedPercentage,
        double averageAttemptsPerTop
) {}
