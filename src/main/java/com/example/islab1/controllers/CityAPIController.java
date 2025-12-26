package com.example.islab1.controllers;

import com.example.islab1.AoP.RateLimit;
import com.example.islab1.Beans.ImportBean;
import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.Entities.City;
import com.example.islab1.Entities.Climate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CityAPIController {
    private final CitiesRepository citiesRepository;

    private final ImportBean importBean;

    public CityAPIController(CitiesRepository citiesRepository, ImportBean importBean) {
        this.citiesRepository = citiesRepository;
        this.importBean = importBean;
    }


    @GetMapping("/getCities")
    public ResponseEntity<?> getCity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String climate,
            @RequestParam(defaultValue = "") String human
    ) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<City> spec = buildSpecification(
                    name, climate, human
            );
            Page<City> cities = citiesRepository.findAll(spec, pageable);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }



    @PostMapping("/addCity")
    @RateLimit
    public ResponseEntity<?> addCity(@RequestBody City city) {
        try {
            citiesRepository.save(city);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
        return ResponseEntity.ok(city);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PatchMapping("/updateCity/{id}")
    @RateLimit
    public ResponseEntity<?> updateCity(
            @PathVariable Long id,
            @RequestBody City updatedCity) {
        try {
            City existingCity = citiesRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Не найден город с id: " + id));
            if (updatedCity.getName() != null) {
                existingCity.setName(updatedCity.getName());
            }
            if (updatedCity.getCoordinates() != null) {
                existingCity.setCoordinates(updatedCity.getCoordinates());
            }
            if (updatedCity.getArea() != null) {
                existingCity.setArea(updatedCity.getArea());
            }
            if (updatedCity.getPopulation() > 0) {
                existingCity.setPopulation(updatedCity.getPopulation());
            }
            if (updatedCity.getEstablishmentDate() != null) {
                existingCity.setEstablishmentDate(updatedCity.getEstablishmentDate());
            }
            if (updatedCity.getCapital() != null) {
                existingCity.setCapital(updatedCity.getCapital());
            }
            if (updatedCity.getMetersAboveSeaLevel() != 0) {
                existingCity.setMetersAboveSeaLevel(updatedCity.getMetersAboveSeaLevel());
            }
            if (updatedCity.getPopulationDensity() != null) {
                existingCity.setPopulationDensity(updatedCity.getPopulationDensity());
            }
            if (updatedCity.getTelephoneCode() != 0) {
                existingCity.setTelephoneCode(updatedCity.getTelephoneCode());
            }
            if (updatedCity.getClimate() != null) {
                existingCity.setClimate(updatedCity.getClimate());
            }
            if (updatedCity.getHuman() != null) {
                existingCity.setHuman(updatedCity.getHuman());
            }

            City savedCity = citiesRepository.save(existingCity);
            return ResponseEntity.ok(savedCity);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping("/deleteCity/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        try {
            if (!citiesRepository.existsById(id)) {
                return ResponseEntity.status(404).body("не найден город с id: " + id);
            }
            citiesRepository.deleteById(id);
            return ResponseEntity.ok().body("City deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }


    private Specification<City> buildSpecification(String nameFilter, String climateFilter, String governorFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (nameFilter != null && !nameFilter.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + nameFilter.toLowerCase() + "%"
                ));
            }

            if (climateFilter != null && !climateFilter.trim().isEmpty()) {
                try {
                    Climate climate = Climate.valueOf(climateFilter.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("climate"), climate));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (governorFilter != null && !governorFilter.trim().isEmpty()) {
                var humanJoin = root.join("human");
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(humanJoin.get("name")),
                        "%" + governorFilter.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping("/importCity")
    @RateLimit
    public ResponseEntity<?> importCity(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Файл пуст");
        }
        return importBean.importFile(file);
    }
}
