package com.example.islab1.controllers;

import com.example.islab1.DBApi.ImportResultRepository;
import com.example.islab1.MinioAPI.MinioService;
import com.example.islab1.entities.ImportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class ImportResultAPIController {
    private final ImportResultRepository importResultRepository;
    private final MinioService minioService;

    public ImportResultAPIController(ImportResultRepository importResultRepository, MinioService minioService) {
        this.importResultRepository = importResultRepository;
        this.minioService = minioService;
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
    @CrossOrigin(exposedHeaders = "Content-Disposition")
    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable long id) {
        try {
            ImportResult importResult = importResultRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Результат импорта с ID " + id + " не найден"));

            String storedFilename = importResult.getStoredFilename();
            if (storedFilename == null || storedFilename.isEmpty()) {
                return ResponseEntity.status(
                        HttpStatus.NOT_FOUND).body(
                        "Файл для импорта с ID " + id + " не был сохранен"
                );
            }

            if (!minioService.fileExists(storedFilename)) {
                return ResponseEntity.status(
                        HttpStatus.NOT_FOUND).body(
                        "Файл '" + storedFilename + "' не найден в MinIO"
                );
            }

            String downloadFilename = importResult.getOriginalFilename();
            if (downloadFilename == null || downloadFilename.isEmpty()) {
                downloadFilename = "import_" + id + ".json";
            }
            System.out.println(downloadFilename);
            return minioService.downloadFileAsAttachment(storedFilename, downloadFilename);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось скачать файл: " + e.getMessage());

        }
    }

}
