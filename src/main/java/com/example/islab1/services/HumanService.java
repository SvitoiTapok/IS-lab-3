package com.example.islab1.services;

import com.example.islab1.AoP.EnableCacheLogging;
import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.DBApi.HumanRepository;
import com.example.islab1.entities.City;
import com.example.islab1.entities.Human;
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
public class HumanService {

    private final HumanRepository humanRepository;
    private final CitiesRepository cityRepository;

    public HumanService(HumanRepository humanRepository, CitiesRepository cityRepository) {
        this.humanRepository = humanRepository;
        this.cityRepository = cityRepository;
    }

    @EnableCacheLogging
    public Page<Human> getHumans(int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return humanRepository.findAll(pageable);
    }
    @EnableCacheLogging
    @Transactional
    public Human addHuman(Human human) {
        return humanRepository.save(human);
    }
    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Human updateHuman(Integer id, Human updatedHuman) {
        Human human = humanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден человек с id: " + id));

        if (updatedHuman.getName() != null) {
            human.setName(updatedHuman.getName());
        }

        return humanRepository.save(human);
    }
    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteHuman(Integer id) {
        if (!humanRepository.existsById(id)) {
            throw new EntityNotFoundException("Не найден человек с id: " + id);
        }
        humanRepository.deleteById(id);
    }
    @EnableCacheLogging
    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<City> getCitiesByHumanId(Integer id) {
        Human human = humanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден человек с id: " + id));
        return cityRepository.findByHuman(human);
    }
}