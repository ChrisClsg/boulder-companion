package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.feedback.RouteFeedbackDto;
import de.clsg.boulder_companion.dto.feedback.UpsertRouteFeedbackRequest;
import de.clsg.boulder_companion.model.Route;
import de.clsg.boulder_companion.model.RouteFeedback;
import de.clsg.boulder_companion.repository.RouteFeedbackRepository;
import de.clsg.boulder_companion.repository.RouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class RouteFeedbackService {

    private final RouteFeedbackRepository routeFeedbackRepository;
    private final RouteRepository routeRepository;

    public RouteFeedbackService(
            RouteFeedbackRepository routeFeedbackRepository,
            RouteRepository routeRepository
    ) {
        this.routeFeedbackRepository = routeFeedbackRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional(readOnly = true)
    public Optional<RouteFeedbackDto> getMyFeedback(String userId, String routeId) {
        return routeFeedbackRepository.findByUserIdAndRouteId(userId, routeId)
                .map(this::toDto);
    }

    @Transactional
    public RouteFeedbackDto upsertMyFeedback(String userId, String routeId, UpsertRouteFeedbackRequest request) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found: " + routeId));

        RouteFeedback feedback = routeFeedbackRepository
                .findByUserIdAndRouteId(userId, routeId)
                .map(existing -> new RouteFeedback(
                        existing.id(),
                        existing.userId(),
                        existing.routeId(),
                        existing.gymId(),
                        request.userRating(),
                        request.difficultyFeedback(),
                        existing.createdAt(),
                        null
                ))
                .orElseGet(() -> new RouteFeedback(
                        null,
                        userId,
                        routeId,
                        route.gymId(),
                        request.userRating(),
                        request.difficultyFeedback(),
                        null,
                        null
                ));

        return toDto(routeFeedbackRepository.save(feedback));
    }

    private RouteFeedbackDto toDto(RouteFeedback feedback) {
        return new RouteFeedbackDto(
                feedback.id(),
                feedback.userId(),
                feedback.routeId(),
                feedback.gymId(),
                feedback.userRating(),
                feedback.difficultyFeedback(),
                feedback.createdAt(),
                feedback.updatedAt()
        );
    }
}
