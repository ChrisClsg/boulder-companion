package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.ClimbLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface ClimbLogRepository extends MongoRepository<ClimbLog, String> {

    List<ClimbLog> findByUserId(String userId);

    boolean existsByUserIdAndRouteIdAndClimbedAtBefore(String userId, String routeId, Instant climbedAt);
}
