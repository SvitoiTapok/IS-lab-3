package com.example.islab1.Beans;

import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.Entities.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryBean {


    private final CitiesRepository citiesRepository;

    @Autowired
    public QueryBean(CitiesRepository citiesRepository) {
        this.citiesRepository = citiesRepository;
    }

    public long countCitiesAboveSeaLevel(double minMeters) {
        try {
            List<City> allCities = citiesRepository.findAll();
            long count = allCities.stream()
                    .filter(city -> city.getMetersAboveSeaLevel() > minMeters)
                    .count();

            return count;
        } catch (Exception e) {
            return -1;
        }
    }

    public List<City> getCitiesWithPopulationLessThan(long maxPopulation) {
        try {
            List<City> allCities = citiesRepository.findAll();
            List<City> filteredCities = allCities.stream()
                    .filter(city -> city.getPopulation() < maxPopulation)
                    .collect(Collectors.toList());

            return filteredCities;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getUniqueTelephoneCodes() {
        try {
            List<City> allCities = citiesRepository.findAll();
            List<Integer> uniqueCodes = allCities.stream()
                    .map(City::getTelephoneCode)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            return uniqueCodes;
        } catch (Exception e) {
            return null;
        }
    }

    public double calculateRouteDistance(Long fromCityId, Long toCityId) {
        try {
            City fromCity = citiesRepository.findById(fromCityId)
                    .orElseThrow(() -> new IllegalArgumentException("Город с ID " + fromCityId + " не найден"));
            City toCity = citiesRepository.findById(toCityId)
                    .orElseThrow(() -> new IllegalArgumentException("Город с ID " + toCityId + " не найден"));

            return calculateDistance(fromCity, toCity);
        } catch (Exception e) {
            return -1;
        }
    }

    public double calculateMaxMinPopulationRoute() {
        try {
            List<City> allCities = citiesRepository.findAll();

            if (allCities.isEmpty()) {
                return -1;
            }

            City maxPopulationCity = allCities.stream()
                    .max(Comparator.comparing(City::getPopulation)).orElseThrow();

            City minPopulationCity = allCities.stream()
                    .min(Comparator.comparing(City::getPopulation)).orElseThrow();
            return calculateDistance(maxPopulationCity, minPopulationCity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private double calculateDistance(City fromCity, City toCity) {
        float x1 = fromCity.getCoordinates().getX();
        float y1 = fromCity.getCoordinates().getY();
        float x2 = toCity.getCoordinates().getX();
        float y2 = toCity.getCoordinates().getY();
        return Math.pow(Math.pow(x1 - x2, 2) + Math.pow(y1 * 1.0 - y2 * 1.0, 2), 0.5);
    }
}
