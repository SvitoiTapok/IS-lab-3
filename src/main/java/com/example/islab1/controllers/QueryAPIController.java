package com.example.islab1.controllers;

import com.example.islab1.entities.City;
import com.example.islab1.beans.QueryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueryAPIController {

    private final QueryBean queryBean;
    @Autowired
    public QueryAPIController(QueryBean queryBean) {
        this.queryBean = queryBean;
    }

    @GetMapping("/countAboveSeaLevel")
    public ResponseEntity<Long> countCitiesAboveSeaLevel(@RequestParam double meters) {
        try {
            Long count = queryBean.countCitiesAboveSeaLevel(meters);
            return ResponseEntity.ok(count);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
    @GetMapping("/citiesWithPopulationLessThan")
    public ResponseEntity<List<City>> getCitiesWithPopulationLessThan(@RequestParam long population) {
        try {
            List<City> list = queryBean.getCitiesWithPopulationLessThan(population);
            return ResponseEntity.ok(list);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/uniqueTelephoneCodes")
    public ResponseEntity<List<Integer>> getUniqueTelephoneCodes() {
        try {
            List<Integer> list = queryBean.getUniqueTelephoneCodes();
            return ResponseEntity.ok(list);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @GetMapping("/calculateRoute")
    public ResponseEntity<Double> calculateRoute(@RequestParam Long fromCityId,
                                                 @RequestParam Long toCityId) {
        try {
            double dist = queryBean.calculateRouteDistance(fromCityId, toCityId);
            return ResponseEntity.ok(dist);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @GetMapping("/maxMinPopulationRoute")
    public ResponseEntity<Double> calculateMaxMinPopulationRoute() {
        try {
            double dist = queryBean.calculateMaxMinPopulationRoute();
            return ResponseEntity.ok(dist);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }


}
