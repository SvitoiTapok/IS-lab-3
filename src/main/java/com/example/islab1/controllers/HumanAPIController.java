package com.example.islab1.controllers;

import com.example.islab1.AoP.EnableCacheLogging;
import com.example.islab1.AoP.RateLimit;
import com.example.islab1.entities.City;
import com.example.islab1.entities.Human;
import com.example.islab1.services.HumanService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HumanAPIController {

    private final HumanService humanService;

    public HumanAPIController(HumanService humanService) {
        this.humanService = humanService;
    }


    @GetMapping("/getHumans")
    public ResponseEntity<?> getHumans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Page<Human> humans = humanService.getHumans(page, size, sortBy, sortOrder);
            return ResponseEntity.ok(humans);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/addHuman")
    @RateLimit

    public ResponseEntity<?> addHuman(@RequestBody Human human) {
        try {
            Human savedHuman = humanService.addHuman(human);
            return ResponseEntity.ok(savedHuman);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PatchMapping("/updateHuman/{id}")
    @RateLimit
    public ResponseEntity<?> updateHuman(
            @PathVariable Integer id,
            @RequestBody Human updatedHuman) {
        try {
            Human savedHuman = humanService.updateHuman(id, updatedHuman);
            return ResponseEntity.ok(savedHuman);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @DeleteMapping("/deleteHuman/{id}")
    @RateLimit
    public ResponseEntity<?> deleteHuman(@PathVariable Integer id) {
        try {
            humanService.deleteHuman(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @GetMapping("/getCitiesByHumanId")
    public ResponseEntity<?> getCitiesByHumanId(@RequestParam int id) {
        try {
            List<City> cities = humanService.getCitiesByHumanId(id);
            return ResponseEntity.ok(cities);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }
}