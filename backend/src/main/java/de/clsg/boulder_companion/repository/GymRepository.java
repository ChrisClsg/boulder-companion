package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.Gym;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends MongoRepository<Gym, String> {
}
