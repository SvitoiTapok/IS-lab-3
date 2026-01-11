package com.example.islab1.services;

import com.example.islab1.AoP.EnableCacheLogging;
import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.beans.ImportBean;
import com.example.islab1.entities.City;
import com.example.islab1.entities.Climate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CityService {

    private final CitiesRepository citiesRepository;
    private final ImportBean importBean;

    public CityService(CitiesRepository citiesRepository, ImportBean importBean) {
        this.citiesRepository = citiesRepository;
        this.importBean = importBean;
    }

    @EnableCacheLogging
    public Page<City> getCities(int page, int size, String sortBy, String sortOrder,
                                String name, String climate, String human) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<City> spec = buildSpecification(name, climate, human);
        return citiesRepository.findAll(spec, pageable);
    }

    @EnableCacheLogging
    @Transactional
    public City addCity(City city) {
        return citiesRepository.save(city);
    }

    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public City updateCity(Long id, City updatedCity) {
        City existingCity = citiesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден город с id: " + id));

        updateCityFields(existingCity, updatedCity);

        return citiesRepository.save(existingCity);
    }

    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteCity(Long id) {
        if (!citiesRepository.existsById(id)) {
            throw new EntityNotFoundException("не найден город с id: " + id);
        }
        citiesRepository.deleteById(id);
    }

    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<?> importCity(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Файл пуст");
        }
        return importBean.importFile(file);
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

    private void updateCityFields(City existingCity, City updatedCity) {
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
    }

    public City getCityById(Long id) {
        return citiesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден город с id: " + id));
    }

    public boolean cityExists(Long id) {
        return citiesRepository.existsById(id);
    }

    public List<City> getAllCities() {
        return citiesRepository.findAll();
    }
}