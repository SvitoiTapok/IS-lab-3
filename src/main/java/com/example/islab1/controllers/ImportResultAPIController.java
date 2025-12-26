package com.example.islab1.controllers;

import com.example.islab1.DBApi.ImportResultRepository;
import com.example.islab1.Entities.City;
import com.example.islab1.Beans.QueryBean;
import com.example.islab1.Entities.Human;
import com.example.islab1.Entities.ImportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImportResultAPIController {
    private final ImportResultRepository importResultRepository;

    public ImportResultAPIController(ImportResultRepository importResultRepository) {
        this.importResultRepository = importResultRepository;
    }

    @GetMapping("/getImports")
    public ResponseEntity<?> getImports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ImportResult> importResults = importResultRepository.findAll(pageable);
            return ResponseEntity.ok(importResults);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Некорректные данные");
        }
    }
}
