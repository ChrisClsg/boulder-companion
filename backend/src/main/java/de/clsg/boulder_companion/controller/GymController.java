package de.clsg.boulder_companion.controller;

import de.clsg.boulder_companion.service.GymService;
import de.clsg.boulder_companion.dto.GymDto;

import org.springframework.http.ResponseEntity;
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
}
