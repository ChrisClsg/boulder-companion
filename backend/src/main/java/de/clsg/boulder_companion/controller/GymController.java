package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.GymService;
import de.clsg.boulder_companion.dto.GymDto;
import de.clsg.boulder_companion.model.Gym;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public ResponseEntity<List<GymDto>> getAllGyms() {
        return ResponseEntity.ok(gymService.getAllGyms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<GymDto>> getGymById(@PathVariable String id) {
        return ResponseEntity.ok(gymService.getGymById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymDto> createGym(@RequestBody GymDto gymDto) {
        Gym gym = gymDto.toGym();
        return ResponseEntity.ok(gymService.createGym(gym));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymDto> updateGym(
            @PathVariable String id,
            @RequestBody GymDto updatedGymDto,
            @AuthenticationPrincipal OAuth2User user) {
        Gym gym = updatedGymDto.toGym();
        return ResponseEntity.ok(gymService.updateGym(id, gym));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGym(@PathVariable String id) {
        gymService.deleteGym(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<GymDto>> getGymsByAdmin(
            @PathVariable String adminId,
            @AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok(gymService.getGymsByAdminId(adminId));
    }
}
