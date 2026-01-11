package com.example.islab1.DBApi;

import com.example.islab1.entities.City;
import com.example.islab1.entities.Coordinates;
import com.example.islab1.entities.Human;
import jakarta.persistence.QueryHint;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface CitiesRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {
    @NotNull
    @Override
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "cityQueryCache")
    })
    Page<City> findAll(@NotNull Specification spec, @NotNull Pageable pageable);
    List<City> findByHuman(Human human);

    List<City> findByCoordinates(Coordinates coordinates);
}
