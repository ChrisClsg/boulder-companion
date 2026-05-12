package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.ClimbingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClimbingHistoryRepository extends MongoRepository<ClimbingHistory, String> {

    Optional<ClimbingHistory> findByIdAndUserId(String id, String userId);

    List<ClimbingHistory> findByUserId(String userId);

    List<ClimbingHistory> findByRouteId(String routeId);

    List<ClimbingHistory> findByGymId(String gymId);
}
