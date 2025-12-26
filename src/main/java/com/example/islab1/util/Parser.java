package com.example.islab1.util;

import com.example.islab1.DTO.DTOCity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parser {

    private static final ObjectMapper JSONObjectMapper = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final CsvMapper CSV_MAPPER = new CsvMapper();

    public static List<Map<String, Object>> parseJsonFile(MultipartFile file) throws Exception {
        return JSONObjectMapper.readValue(file.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    public static List<Map<String, Object>> parseXmlFile(MultipartFile file) throws Exception {
        return convertValues(XML_MAPPER.readValue(
                file.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        ));
    }

    public static List<Map<String, Object>> parseCsvFile(MultipartFile file) throws Exception {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        return convertValues(CSV_MAPPER.readerFor(Map.class)
                .with(schema)
                .<Map<String, Object>>readValues(file.getInputStream())
                .readAll());

    }


    private static List<Map<String, Object>> convertValues(List<Map<String, Object>> rawList) {
        List<Map<String, Object>> convertedList = new ArrayList<>();

        for (Map<String, Object> rawMap : rawList) {
            Map<String, Object> convertedMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                String stringValue = (String) value;
                Object convertedValue = convertStringToType(stringValue);
                convertedMap.put(key, convertedValue);
            }
            convertedList.add(convertedMap);
        }
        return convertedList;
    }

    public static Object convertStringToType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmedValue = value.trim();

        if ("true".equalsIgnoreCase(trimmedValue) || "false".equalsIgnoreCase(trimmedValue)) {
            return Boolean.parseBoolean(trimmedValue.toLowerCase());
        }

        try {
            if (trimmedValue.matches("-?\\d+")) {
                long longValue = Long.parseLong(trimmedValue);
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    return (int) longValue;
                }
                return longValue;
            }
        } catch (NumberFormatException e) {
            // Не целое число
        }

        try {
            String normalizedValue = trimmedValue.replace(',', '.');
            if (normalizedValue.matches("-?\\d+(\\.\\d+)?")) {
                double doubleValue = Double.parseDouble(normalizedValue);
                if (normalizedValue.contains(".") && doubleValue == Math.floor(doubleValue)) {
                    long longValue = (long) doubleValue;
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    }
                    return longValue;
                }
                return doubleValue;
            }
        } catch (NumberFormatException e) {
            // Не число с плавающей точкой
        }
        return trimmedValue;
    }

    public static void printListOfMaps(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            sb.append("  {\n");

            int entryCount = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append("    ")
                        .append(escapeJson(entry.getKey()))
                        .append(": ")
                        .append(formatValue(entry.getValue()));

                if (++entryCount < map.size()) {
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("  }");
            if (i < list.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("]");
        System.out.println(sb.toString());
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return escapeJson((String) value);
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
//        if (value instanceof List) {
//            return formatList((List<?>) value);
//        }
//        if (value instanceof Map) {
//            return formatMap((Map<?, ?>) value);
//        }
        return escapeJson(value.toString());
    }

    private static String escapeJson(String str) {
        return "\"" + str.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }


}
