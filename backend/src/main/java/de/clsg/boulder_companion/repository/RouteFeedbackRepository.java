package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.RouteFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteFeedbackRepository extends MongoRepository<RouteFeedback, String> {

    Optional<RouteFeedback> findByUserIdAndRouteId(String userId, String routeId);
}
