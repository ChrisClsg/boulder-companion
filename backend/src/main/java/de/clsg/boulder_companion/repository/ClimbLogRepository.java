package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.ClimbLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClimbLogRepository extends MongoRepository<ClimbLog, String> {

    Optional<ClimbLog> findByIdAndUserId(String id, String userId);

    List<ClimbLog> findByUserId(String userId);

    List<ClimbLog> findByRouteId(String routeId);

    List<ClimbLog> findByGymId(String gymId);

    boolean existsByUserIdAndRouteIdAndClimbedAtBefore(String userId, String routeId, Instant climbedAt);
}
