package com.example.islab1.controllers;

import com.example.islab1.AoP.RateLimit;
import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.DBApi.CoordinatesRepository;
import com.example.islab1.Entities.City;
import com.example.islab1.Entities.Coordinates;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CoordinatesAPIController {

    private final CoordinatesRepository coordinatesRepository;
    private final CitiesRepository cityRepository;

    public CoordinatesAPIController(CoordinatesRepository coordinatesRepository, CitiesRepository cityRepository) {
        this.coordinatesRepository = coordinatesRepository;
        this.cityRepository = cityRepository;
    }


    @GetMapping("/getCoordinates")
    public ResponseEntity<?> getCoordinates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Coordinates> coordinates = coordinatesRepository.findAll(pageable);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/addCoordinates")
    @RateLimit
    public ResponseEntity<?> addCoordinates(@RequestBody Coordinates coordinates) {
        try {
            coordinatesRepository.save(coordinates);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
        return ResponseEntity.ok(coordinates);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PatchMapping("/updateCoord/{id}")
    @RateLimit
    public ResponseEntity<?> updateCoord(
            @PathVariable Integer id,
            @RequestBody Coordinates updatedCoordinates) {
        try {
            Coordinates coordinates = coordinatesRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Не найдены координаты с id: " + id));
            if (updatedCoordinates.getX() != null) {
                coordinates.setX(updatedCoordinates.getX());
            }
            if (updatedCoordinates.getY() > -563) {
                coordinates.setY(updatedCoordinates.getY());
            }

            Coordinates savedCoord = coordinatesRepository.save(coordinates);
            return ResponseEntity.ok(savedCoord);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping("/deleteCoord/{id}")
    @RateLimit
    public ResponseEntity<?> deleteCoord(@PathVariable Integer id) {
        try {
            if (!coordinatesRepository.existsById(id)) {
                return ResponseEntity.status(404).body("Не найдены координаты с id: " + id);
            }
            coordinatesRepository.deleteById(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @GetMapping("/getCitiesByCoordId")
    public ResponseEntity<?> getCitiesByCoordId(
            @RequestParam int id) {
        try {
            Coordinates coordinates = coordinatesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Не найдены координаты с id: " + id));
            ;

            List<City> cities = cityRepository.findByCoordinates(coordinates);
            return ResponseEntity.ok(cities);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }
}