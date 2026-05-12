package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    List<Route> findByGymId(String gymId);

    Optional<Route> findByIdAndGymId(String id, String gymId);

    List<Route> findByGymIdAndArchived(String gymId, boolean archived);
}
