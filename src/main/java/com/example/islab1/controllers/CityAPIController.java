package com.example.islab1.controllers;

import com.example.islab1.AoP.EnableCacheLogging;
import com.example.islab1.AoP.RateLimit;
import com.example.islab1.entities.City;
import com.example.islab1.services.CityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CityAPIController {

    private final CityService cityService;

    public CityAPIController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/getCities")
    public ResponseEntity<?> getCity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String climate,
            @RequestParam(defaultValue = "") String human) {
        try {
            Page<City> cities = cityService.getCities(page, size, sortBy, sortOrder, name, climate, human);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/addCity")
    @RateLimit
    public ResponseEntity<?> addCity(@RequestBody City city) {
        try {
            City savedCity = cityService.addCity(city);
            return ResponseEntity.ok(savedCity);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PatchMapping("/updateCity/{id}")
    @RateLimit
    public ResponseEntity<?> updateCity(
            @PathVariable Long id,
            @RequestBody City updatedCity) {
        try {
            City savedCity = cityService.updateCity(id, updatedCity);
            return ResponseEntity.ok(savedCity);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @DeleteMapping("/deleteCity/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        try {
            cityService.deleteCity(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/importCity")
    @RateLimit
    public ResponseEntity<?> importCity(@RequestParam("file") MultipartFile file) {
        try {
            return cityService.importCity(file);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Ошибка при импорте файла");
        }
    }
}