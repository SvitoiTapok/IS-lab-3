package com.example.islab1.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Table(name = "human")
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name",
            columnDefinition = "TEXT NOT NULL CHECK (LENGTH(name) > 0)")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @OneToMany(mappedBy = "human")
    @JsonIgnore
    private List<City> cities = new ArrayList<>();
}
