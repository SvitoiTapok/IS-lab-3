package com.example.islab1.controllers;

import com.example.islab1.AoP.RateLimit;
import com.example.islab1.entities.City;
import com.example.islab1.entities.Coordinates;
import com.example.islab1.services.CoordinatesService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CoordinatesAPIController {

    private final CoordinatesService coordinatesService;

    public CoordinatesAPIController(CoordinatesService coordinatesService) {
        this.coordinatesService = coordinatesService;
    }

    @GetMapping("/getCoordinates")
    public ResponseEntity<?> getCoordinates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Page<Coordinates> coordinates = coordinatesService.getCoordinates(page, size, sortBy, sortOrder);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/addCoordinates")
    @RateLimit
    public ResponseEntity<?> addCoordinates(@RequestBody Coordinates coordinates) {
        try {
            Coordinates savedCoordinates = coordinatesService.addCoordinates(coordinates);
            return ResponseEntity.ok(savedCoordinates);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PatchMapping("/updateCoord/{id}")
    @RateLimit
    public ResponseEntity<?> updateCoord(
            @PathVariable Integer id,
            @RequestBody Coordinates updatedCoordinates) {
        try {
            Coordinates savedCoordinates = coordinatesService.updateCoordinates(id, updatedCoordinates);
            return ResponseEntity.ok(savedCoordinates);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @DeleteMapping("/deleteCoord/{id}")
    @RateLimit
    public ResponseEntity<?> deleteCoord(@PathVariable Integer id) {
        try {
            coordinatesService.deleteCoordinates(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @GetMapping("/getCitiesByCoordId")
    public ResponseEntity<?> getCitiesByCoordId(@RequestParam int id) {
        try {
            List<City> cities = coordinatesService.getCitiesByCoordinatesId(id);
            return ResponseEntity.ok(cities);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }
}