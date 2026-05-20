package de.clsg.boulder_companion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring context smoke test.
 * Test properties (including embedded MongoDB and OAuth2 stubs) are loaded
 * from src/test/resources/application.properties via Spring Boot's standard
 * test configuration convention.
 */
@SpringBootTest
class BoulderCompanionApplicationTests {

    @Test
    void contextLoads() {
    }

}
