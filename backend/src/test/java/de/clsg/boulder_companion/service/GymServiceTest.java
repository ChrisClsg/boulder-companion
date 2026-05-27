package de.clsg.boulder_companion.service;

import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.model.Gym;
import de.clsg.boulder_companion.repository.GymRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GymService.
 * GymRepository is mocked; only mapping/delegation logic is tested.
 */
@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private GymService gymService;

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    private Gym makeGym(String id, String name) {
        return new Gym(id, name, "Address", "Description", "0800", "https://example.com",
                null, "admin-1", NOW, NOW);
    }

    // -------------------------------------------------------------------------
    // getAllGyms
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getAllGyms()")
    class GetAllGyms {

        @Test
        @DisplayName("returns empty list when no gyms exist")
        void getAllGyms_noGyms_returnsEmptyList() {
            when(gymRepository.findAll()).thenReturn(List.of());

            List<GymDto> result = gymService.getAllGyms();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns all gyms mapped to DTOs")
        void getAllGyms_multipleGyms_returnsMappedDtos() {
            Gym g1 = makeGym("g1", "Boulderhaus");
            Gym g2 = makeGym("g2", "Kletterhalle");
            when(gymRepository.findAll()).thenReturn(List.of(g1, g2));

            List<GymDto> result = gymService.getAllGyms();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(GymDto::id).containsExactly("g1", "g2");
            assertThat(result).extracting(GymDto::name).containsExactly("Boulderhaus", "Kletterhalle");
        }

        @Test
        @DisplayName("maps all gym fields correctly")
        void getAllGyms_singleGym_allFieldsMappedCorrectly() {
            Gym gym = new Gym("g1", "Boulderhaus", "Main St 1", "Cool gym", "0800-123",
                    "https://boulder.de", new Gym.OpeningHours("9-21", "9-21", "9-21", "9-21", "9-21", "10-20", "closed"),
                    "admin-1", NOW, NOW);
            when(gymRepository.findAll()).thenReturn(List.of(gym));

            List<GymDto> result = gymService.getAllGyms();

            GymDto dto = result.get(0);
            assertThat(dto.id()).isEqualTo("g1");
            assertThat(dto.name()).isEqualTo("Boulderhaus");
            assertThat(dto.address()).isEqualTo("Main St 1");
            assertThat(dto.adminId()).isEqualTo("admin-1");
            assertThat(dto.openingHours()).isNotNull();
            assertThat(dto.openingHours().monday()).isEqualTo("9-21");
        }
    }

    // -------------------------------------------------------------------------
    // getGymById
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getGymById()")
    class GetGymById {

        @Test
        @DisplayName("returns the gym DTO when found")
        void getGymById_existingId_returnsGymDto() {
            Gym gym = makeGym("g1", "Boulderhaus");
            when(gymRepository.findById("g1")).thenReturn(Optional.of(gym));

            Optional<GymDto> result = gymService.getGymById("g1");

            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo("g1");
            assertThat(result.get().name()).isEqualTo("Boulderhaus");
        }

        @Test
        @DisplayName("returns empty Optional when gym not found")
        void getGymById_unknownId_returnsEmptyOptional() {
            when(gymRepository.findById("unknown")).thenReturn(Optional.empty());

            Optional<GymDto> result = gymService.getGymById("unknown");

            assertThat(result).isEmpty();
        }
    }
}
