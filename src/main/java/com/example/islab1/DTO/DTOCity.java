package com.example.islab1.DTO;

import com.example.islab1.Entities.City;
import com.example.islab1.Entities.Climate;
import com.example.islab1.Entities.Coordinates;
import com.example.islab1.Entities.Human;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DTOCity {
    @NotBlank(message = "Название города обязательно")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Positive(message = "id координат должно быть положительным числом")
    private Integer coordinatesID; //Поле не может быть null

    @Positive(message = "Значение площади должно быть больше 0")
    private Long area; //Значение поля должно быть больше 0, Поле не может быть null

    @Positive(message = "Значение населения должно быть больше 0")
    private int population; //Значение поля должно быть больше 0, Поле не может быть null

    private java.time.LocalDateTime establishmentDate;

    @NotNull(message = "столица должна быть boolean")
    private Boolean capital; //Поле не может быть null


    private Integer metersAboveSeaLevel;

    @Positive(message = "Значение плотности населения должно быть больше 0")
    private Long populationDensity; //Значение поля должно быть больше 0

    @Positive(message = "Значение телефонного кода должно быть больше 0")
    @Max(value = 100000, message = "Значение телефонного кода должно быть меньше 100000")
    private Integer telephoneCode; //Значение поля должно быть больше 0, Максимальное значение поля: 100000

    @NotNull(message = "climate должен быть не null")
    private Climate climate; //Поле может быть null

    @Positive(message = "human_id должен быть больше нуля")
    private Integer humanID; //Поле не может быть null

    private Human human; //Поле не может быть null
    private Coordinates coordinates; //Поле не может быть null

    public void setFieldByName(String name, Object value) {
        switch (name) {
            case "name":
                this.name = (String) value;
                break;
            case "area":
                this.area = ((Integer) value).longValue();
                break;
            case "population":
                this.population = (Integer) value;
                break;
            case "establishmentDate":
                this.establishmentDate = java.time.LocalDateTime.parse(value.toString());
                break;
            case "capital":
                this.capital = (Boolean) value;
                break;
            case "metersAboveSeaLevel":
                this.metersAboveSeaLevel = (Integer) value;
                break;
            case "populationDensity":
                this.populationDensity = ((Integer) value).longValue();
                break;
            case "telephoneCode":
                this.telephoneCode = (Integer) value;
                break;
            case "climate":
                this.climate = Climate.valueOf(value.toString());
                break;
            case "humanID":
                this.humanID = (Integer) value;
                break;
            case "coordinatesID":
                this.coordinatesID = (Integer) value;
                break;

        }
    }
    public City toCity() {
        City city = new City();
        city.setName(this.name);
        city.setArea(this.area);
        city.setPopulation(this.population);
        city.setEstablishmentDate(this.establishmentDate);
        city.setCapital(this.capital);
        city.setCoordinates(this.coordinates);
        city.setPopulationDensity(this.populationDensity);
        city.setTelephoneCode(this.telephoneCode);
        city.setClimate(this.climate);
        city.setHuman(this.human);
        city.setMetersAboveSeaLevel(this.metersAboveSeaLevel);
        return city;
    }
}
