package com.example.islab1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ImportResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer status;

    @Column(columnDefinition = "TEXT")
    private String description; //Поле не может быть null, Строка не может быть пустой


    @CreationTimestamp
    @Column(name = "creationDate", updatable = false, nullable = false)
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @JsonIgnore
    @Column(name = "stored_filename")
    private String storedFilename;

    @Column(name = "original_filename")
    private String originalFilename;

}
