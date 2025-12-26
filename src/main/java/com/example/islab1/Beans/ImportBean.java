package com.example.islab1.Beans;

import com.example.islab1.DBApi.CitiesRepository;
import com.example.islab1.DBApi.CoordinatesRepository;
import com.example.islab1.DBApi.HumanRepository;
import com.example.islab1.DBApi.ImportResultRepository;
import com.example.islab1.DTO.DTOCity;
import com.example.islab1.Entities.ImportResult;
import com.example.islab1.util.Parser;
import com.example.islab1.util.StringComparator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ImportBean {
    private final CitiesRepository citiesRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final HumanRepository humanRepository;
    private final ImportResultRepository importResultRepository;
    private final Validator validator;

    @Autowired
    public ImportBean(CitiesRepository citiesRepository, HumanRepository humanRepository, CoordinatesRepository coordinatesRepository, ImportResultRepository importResultRepository) {
        this.citiesRepository = citiesRepository;
        this.humanRepository = humanRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.importResultRepository = importResultRepository;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public ResponseEntity<?> importFile(MultipartFile file) {
        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename();

        List<Map<String, Object>> objects;
        try {
            if (originalName.endsWith(".json") ||
                    "application/json".equals(contentType)) {
                objects = Parser.parseJsonFile(file);

            } else if (originalName.endsWith(".xml") ||
                    "application/xml".equals(contentType)) {
                objects = Parser.parseXmlFile(file);
            } else if (originalName.endsWith(".csv") ||
                    "text/csv".equals(contentType)) {
                objects = Parser.parseCsvFile(file);
            } else {
                return ResponseEntity.badRequest()
                        .body("Неподдерживаемый формат файла");
            }
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest()
                    .body("Неподдерживаемый формат файла");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Невозможно получить массив объектов");
        }
        try {
            Parser.printListOfMaps(objects);
            List<DTOCity> cities = new ArrayList<>();
            String responseMessage = "";
            for (int i = 0; i < objects.size(); i++) {
                List<String> stringFieldNames = new ArrayList<>(List.of("name", "climate", "establishmentDate"));
                List<String> boolFieldNames = new ArrayList<>(List.of("capital"));
                List<String> numberFieldNames = new ArrayList<>(List.of("coordinatesID", "area", "population", "metersAboveSeaLevel", "telephoneCode", "humanID", "populationDensity"));
                //если не совпадает количество аргументов - джиджис
                if (objects.get(i).size() != 11) return returnAndSave(HttpStatus.BAD_REQUEST, "Несоответствующее количество полей в записи номер " + (i + 1));
                Map<String, Object> currentObj = objects.get(i);

                DTOCity currentCity = new DTOCity();
                for (String fieldName : currentObj.keySet()) {
                    List<String> source;
                    if (currentObj.get(fieldName) instanceof Number) source = numberFieldNames;
                    else if (currentObj.get(fieldName) instanceof String) source = stringFieldNames;
                    else if (currentObj.get(fieldName) instanceof Boolean) source = boolFieldNames;
                    else
                        return returnAndSave(HttpStatus.BAD_REQUEST, "Неподдерживаемый формат значения поля " + fieldName + " в объекте под номером " + (i + 1));
                    if (source.isEmpty())
                        return returnAndSave(HttpStatus.BAD_REQUEST,"Несоответствующее количество полей определенного типа в объекте с номерном " + (i + 1));
                    double value = -1;
                    String mostSimilar = null;
                    for (String field : source) {
                        double x = StringComparator.similarity(field, fieldName);
                        if (x > value) {
                            value = x;
                            mostSimilar = field;
                        }
                    }
                    if (value < 0.7)
                        return returnAndSave(HttpStatus.BAD_REQUEST,"Не найдено совпадений для поля " + fieldName + " в объекте под номером " + (i + 1));
                    try {
                        currentCity.setFieldByName(mostSimilar, currentObj.get(fieldName));
                    } catch (IllegalArgumentException e) {
                        return returnAndSave(HttpStatus.BAD_REQUEST,"Климат в объекте с номером " + (i+1) + " не предусмотрен системой");
                    }

                    if (value != 1) {
                        responseMessage = responseMessage + "В записи номер " + (i + 1) + " название поля " + fieldName + " воспринято как " + mostSimilar + ".\n";
                    }
                    source.remove(mostSimilar);
                }
                Set<ConstraintViolation<DTOCity>> violations = validator.validate(currentCity);
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining(", "));
                    return returnAndSave(HttpStatus.BAD_REQUEST,"Ошибка валидации: " + errorMessage);
                }
                try {
                    int finalI = i;
                    currentCity.setHuman(humanRepository.findById(currentCity.getHumanID()).orElseThrow(() -> new IllegalArgumentException("Человек с ID " + currentCity.getHumanID() + " не найден(запись №"+(finalI +1)+")")));
                    currentCity.setCoordinates(coordinatesRepository.findById(currentCity.getHumanID()).orElseThrow(() -> new IllegalArgumentException("Координаты с ID " + currentCity.getHumanID() + " не найден(запись №"+(finalI +1)+")")));
                }catch (Exception e){
                    return returnAndSave(HttpStatus.NOT_FOUND, e.getMessage());
                }
                cities.add(currentCity);
            }
            cities.stream().map(DTOCity::toCity).forEach(citiesRepository::save);
            return returnAndSave(HttpStatus.OK, "Успешно добавлено " + cities.size() + " объектов" + ((responseMessage.isEmpty())?"":(". Список измененных полей: \n" + responseMessage)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка обработки файла: " + e.getMessage());
        }
    }
    private ResponseEntity<?> returnAndSave(HttpStatus status, String message) {
        ImportResult result = new ImportResult();
        result.setDescription(message);
        result.setStatus(status.value());
        importResultRepository.save(result);
        if (status == HttpStatus.OK) return ResponseEntity.ok("Объекты успешно добавлены, см. историю добавления");
        return ResponseEntity.status(status).body(message);
    }
}
