package com.example.islab1.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Float x;
    @Column(name = "y",
            columnDefinition = "REAL CHECK(y>-563)")
    private float y; //Значение поля должно быть больше -563
    @OneToMany(mappedBy = "coordinates")
    @JsonIgnore
    private List<City> cities = new ArrayList<>();
}
