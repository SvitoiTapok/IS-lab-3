package com.example.islab1.services;

import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.DBApi.CoordinatesRepository;
import com.example.islab1.entities.City;
import com.example.islab1.entities.Coordinates;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final CitiesRepository cityRepository;

    public CoordinatesService(CoordinatesRepository coordinatesRepository, CitiesRepository cityRepository) {
        this.coordinatesRepository = coordinatesRepository;
        this.cityRepository = cityRepository;
    }

    public Page<Coordinates> getCoordinates(int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return coordinatesRepository.findAll(pageable);
    }

    @Transactional
    public Coordinates addCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Coordinates updateCoordinates(Integer id, Coordinates updatedCoordinates) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдены координаты с id: " + id));

        if (updatedCoordinates.getX() != null) {
            coordinates.setX(updatedCoordinates.getX());
        }
        if (updatedCoordinates.getY() > -563) {
            coordinates.setY(updatedCoordinates.getY());
        }

        return coordinatesRepository.save(coordinates);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteCoordinates(Integer id) {
        if (!coordinatesRepository.existsById(id)) {
            throw new EntityNotFoundException("Не найдены координаты с id: " + id);
        }
        coordinatesRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<City> getCitiesByCoordinatesId(Integer id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдены координаты с id: " + id));
        return cityRepository.findByCoordinates(coordinates);
    }

    public Coordinates getCoordinatesById(Integer id) {
        return coordinatesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдены координаты с id: " + id));
    }

    public boolean coordinatesExist(Integer id) {
        return coordinatesRepository.existsById(id);
    }

    public List<Coordinates> getAllCoordinates() {
        return coordinatesRepository.findAll();
    }
}