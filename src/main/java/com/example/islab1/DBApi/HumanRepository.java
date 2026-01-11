package com.example.islab1.DBApi;

import com.example.islab1.entities.Human;
import jakarta.persistence.QueryHint;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;


@Repository
public interface HumanRepository extends JpaRepository<Human, Integer> {
    @NotNull
    @Override
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "humansQueryCache")
    })
    Page<Human> findAll(@NotNull Pageable pageable);


}
