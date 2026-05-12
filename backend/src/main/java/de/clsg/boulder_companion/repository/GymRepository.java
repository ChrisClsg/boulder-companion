package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.Gym;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GymRepository extends MongoRepository<Gym, String> {

    Optional<Gym> findByIdAndAdminId(String id, String adminId);

    List<Gym> findByAdminId(String adminId);
}
