package com.example.islab1.DBApi;

import com.example.islab1.Entities.City;
import com.example.islab1.Entities.Coordinates;
import com.example.islab1.Entities.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CitiesRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {
    List<City> findByHuman(Human human);

    List<City> findByCoordinates(Coordinates coordinates);
}
