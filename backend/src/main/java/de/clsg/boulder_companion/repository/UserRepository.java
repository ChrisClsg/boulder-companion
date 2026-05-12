package de.clsg.boulder_companion.repository;

import de.clsg.boulder_companion.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByGithubId(String githubId);

    boolean existsByGithubId(String githubId);
}
