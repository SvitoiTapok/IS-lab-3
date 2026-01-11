package com.example.islab1.DBApi;

import com.example.islab1.entities.Coordinates;
import com.example.islab1.entities.ImportResult;
import jakarta.persistence.QueryHint;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface ImportResultRepository extends JpaRepository<ImportResult, Long> {
    @NotNull
    @Override
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "importQueryCache")
    })
    Page<ImportResult> findAll(@NotNull Pageable pageable);
}
