package com.example.islab1.controllers;

import com.example.islab1.AoP.RateLimit;
import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.DBApi.HumanRepository;
import com.example.islab1.Entities.City;
import com.example.islab1.Entities.Human;
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
public class HumanAPIController {
    private final HumanRepository humanRepository;
    private final CitiesRepository cityRepository;

    public HumanAPIController(CitiesRepository cityRepository, HumanRepository humanRepository) {
        this.cityRepository = cityRepository;
        this.humanRepository = humanRepository;
    }

    @GetMapping("/getHumans")
    public ResponseEntity<?> getHumans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Human> humans = humanRepository.findAll(pageable);
            return ResponseEntity.ok(humans);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @PostMapping("/addHuman")
    @RateLimit
    public ResponseEntity<?> addHuman(@RequestBody Human human) {
        try {
            humanRepository.save(human);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
        return ResponseEntity.ok(human);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PatchMapping("/updateHuman/{id}")
    @RateLimit
    public ResponseEntity<?> updateHuman(
            @PathVariable Integer id,
            @RequestBody Human updatedHuman) {
        try {
            Human human = humanRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Не найден человек с id: " + id));
            if (human.getName() != null) {
                human.setName(updatedHuman.getName());
            }

            Human savedHuman = humanRepository.save(human);
            return ResponseEntity.ok(savedHuman);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping("/deleteHuman/{id}")
    @RateLimit
    public ResponseEntity<?> deleteHuman(@PathVariable Integer id) {
        try {
            if (!humanRepository.existsById(id)) {
                return ResponseEntity.status(404).body("Не найден человек с id: " + id);
            }
            humanRepository.deleteById(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @GetMapping("/getCitiesByHumanId")
    public ResponseEntity<?> getCitiesByHumanId(
            @RequestParam int id) {
        try {
            Human human = humanRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Не найден человек с id: " + id));
            ;
            List<City> cities = cityRepository.findByHuman(human);
            return ResponseEntity.ok(cities);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }


}
