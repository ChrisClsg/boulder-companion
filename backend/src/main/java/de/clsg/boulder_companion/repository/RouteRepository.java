package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    List<Route> findByGymId(String gymId);
}
