package com.example.islab1.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(name = "name",
            columnDefinition = "TEXT NOT NULL CHECK (LENGTH(name) > 0)")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @CreationTimestamp
    @Column(name = "creationDate", updatable = false, nullable = false)
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Column(name = "area",
            columnDefinition = "BIGINT NOT NULL CHECK(area>0)")
    private Long area; //Значение поля должно быть больше 0, Поле не может быть null

    @Column(name = "population",
            columnDefinition = "INTEGER NOT NULL CHECK(population>0)")
    private int population; //Значение поля должно быть больше 0, Поле не может быть null

    private java.time.LocalDateTime establishmentDate;

    @NotNull
    private Boolean capital; //Поле не может быть null

    private Integer metersAboveSeaLevel;
    @Column(name = "population_density",
            columnDefinition = "BIGINT CHECK(population_density>0)")
    private Long populationDensity; //Значение поля должно быть больше 0

    @Column(name = "telephoneCode",
            columnDefinition = "INTEGER CHECK(telephone_code>0 AND telephone_code<100001)")
    private Integer telephoneCode; //Значение поля должно быть больше 0, Максимальное значение поля: 100000

    @NotNull
    private Climate climate; //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "human_id", nullable = false)
    private Human human; //Поле не может быть null



}
