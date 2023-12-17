package eu.ciechanowiec.templater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import eu.ciechanowiec.templater.model.WeatherCondition;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonParser {

    private final ObjectMapper objectMapper;

    JsonParser() {
        objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    Map<String, WeatherCondition> parseConditionsFromHtmlMap(String filePath) {
        File jsonFile = new File(filePath);

        Map<String, Object> allData = objectMapper.readValue(jsonFile, new TypeReference<>() {});
        Map<String, WeatherCondition> conditions = new HashMap<>();

        Map<String, Object> conditionMappingsObj = (Map<String, Object>) allData.get("conditionMappings");
        if (conditionMappingsObj != null) {
            for (Map.Entry<String, Object> entry : conditionMappingsObj.entrySet()) {
                Map<String, String> details = (Map<String, String>) entry.getValue();
                String iconName = details.get("iconName");
                String customColor = details.get("customColor");
                conditions.put(entry.getKey(), new WeatherCondition(iconName, customColor));
            }
        }

        return conditions;
    }

    @SneakyThrows
    Map<String, String> parseConfigDataFromHtmlMap(String filePath) {
        File jsonFile = new File(filePath);

        Map<String, Object> allData = objectMapper.readValue(jsonFile, new TypeReference<>() {});

        Map<String, String> configData = new HashMap<>();

        for (Map.Entry<String, Object> entry : allData.entrySet()) {
            Object valueFromMap = entry.getValue();
            if (valueFromMap instanceof String stringValue) {
                configData.put(entry.getKey(), stringValue);
            }
        }

        return configData;
    }
}
