package eu.ciechanowiec.templater.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ciechanowiec.JsonPathsConstants;
import eu.ciechanowiec.templater.model.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//TODO: extract time normally
@Component
public class WeatherLocationRetriever {

    private final UpcomingTimeAndDayCalculator upcomingTimeAndDayCalculator;

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.url}")
    private String url;
    private final ObjectMapper objectMapper;
    private final WeatherApiClient weatherApiClient;

    @Autowired
    public WeatherLocationRetriever(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
        upcomingTimeAndDayCalculator = new UpcomingTimeAndDayCalculator();
        objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    public WeatherData retrieve(double longitude, double latitude) throws JsonProcessingException {
        String responseBody = weatherApiClient.getResponse(latitude, longitude);
        System.out.println(responseBody.length());

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode locationNode = rootNode.path(JsonPathsConstants.LOCATION);
        JsonNode currentWeatherNode = rootNode.path(JsonPathsConstants.CURRENT);
        String tempC = currentWeatherNode.path(JsonPathsConstants.TEMP_C).asText();
        String condition = currentWeatherNode.path(JsonPathsConstants.CONDITION).path(JsonPathsConstants.TEXT).asText();
        String dateAndTime = currentWeatherNode.path(JsonPathsConstants.LAST_UPDATE).asText();

        String timeStr = dateAndTime.substring(11, 16);
        LocalTime time = LocalTime.parse(timeStr);

        String city = locationNode.path(JsonPathsConstants.NAME).asText();
        String country = locationNode.path(JsonPathsConstants.COUNTRY).asText();
        String location = city + ", " + country;
        String currentDay = extractDayOfWeek(rootNode);

        CurrentWeatherData currentWeatherData = new CurrentWeatherData(condition, currentDay, location, tempC, time);
        ForecastTemperatures forecastTemperatures = createTemperaturesForecast(responseBody, time);
        ForecastConditions forecastConditions = createConditionsForecast(responseBody, time);
        ForecastWeatherData forecastWeatherData = new ForecastWeatherData(forecastConditions, forecastTemperatures);

        return new WeatherData(currentWeatherData, forecastWeatherData);
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static String extractDayOfWeek(JsonNode rootNode) {
        String localtime = rootNode.path(JsonPathsConstants.LOCATION).path(JsonPathsConstants.LOCAL_TIME).asText();
        LocalDate date = LocalDate.parse(localtime, DateTimeFormatter.ofPattern("yyyy-MM-dd [H][HH]:mm"));
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.toString();
    }

    private ForecastTemperatures createTemperaturesForecast(String jsonResponse, LocalTime timeStamp) throws JsonProcessingException {
        int[] upcomingHours = upcomingTimeAndDayCalculator.calculateNextTenHours(timeStamp);
        int day = 0;
        String[] hourByHourForecast = new String[5];

        for (int i = 0; i < upcomingHours.length; i++) {
            if (upcomingHours[i] <= 2) {
                day++;
            }
            hourByHourForecast[i] = extractTemperature(jsonResponse, upcomingHours[i], day);
        }

        String firstDayTemperature =
                retrieveWeatherDetailByDay(jsonResponse, 1, JsonPathsConstants.TEMP);
        String secondDayTemperature =
                retrieveWeatherDetailByDay(jsonResponse, 2, JsonPathsConstants.TEMP);

        return new ForecastTemperatures(hourByHourForecast[0], hourByHourForecast[1], hourByHourForecast[2],
                hourByHourForecast[3], hourByHourForecast[4],
                firstDayTemperature, secondDayTemperature);
    }

    private ForecastConditions createConditionsForecast(String jsonResponse, LocalTime timeStamp) throws JsonProcessingException {
        int[] upcomingHours = upcomingTimeAndDayCalculator.calculateNextTenHours(timeStamp);
        int day = 0;
        String[] hourByHourForecast = new String[5];

        for (int i = 0; i < upcomingHours.length; i++) {
            if (upcomingHours[i] <= 2) {
                day++;
            }
            String upcomingHoursStr = Integer.toString(upcomingHours[i]);
            hourByHourForecast[i] = extractCondition(jsonResponse, upcomingHoursStr, day);
        }

        String secondDayCondition = retrieveWeatherDetailByDay(jsonResponse, 1, JsonPathsConstants.CONDITION);
        String thirdDayCondition = retrieveWeatherDetailByDay(jsonResponse, 2, JsonPathsConstants.CONDITION);

        return new ForecastConditions(hourByHourForecast[0], hourByHourForecast[1], hourByHourForecast[2],
                hourByHourForecast[3], hourByHourForecast[4],
                secondDayCondition, thirdDayCondition);
    }

    private String extractTemperature(String jsonResponse, int hourToFind, int dayIndexToFind) throws JsonProcessingException {
        String hourStr = String.valueOf(hourToFind);
        JsonNode hourNode = extractHourNode(jsonResponse, hourStr, dayIndexToFind);
        return hourNode.path(JsonPathsConstants.TEMP_C).asText();
    }

    private String extractCondition(String jsonResponse, String hourToFind, int dayIndexToFind) throws JsonProcessingException {
        JsonNode hourNode = extractHourNode(jsonResponse, hourToFind, dayIndexToFind);
        return hourNode.path(JsonPathsConstants.CONDITION).path(JsonPathsConstants.TEXT).asText();
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    private JsonNode extractHourNode(String jsonResponse, String hourToFind, int dayIndexToFind) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode forecastNode = rootNode.path(JsonPathsConstants.FORECAST)
                .path(JsonPathsConstants.FORECAST_DAY).get(dayIndexToFind);
        return findHourNode(forecastNode.path(JsonPathsConstants.HOUR), hourToFind);
    }


    @SneakyThrows
    private String retrieveWeatherDetailByDay(String jsonResponse, int dayIndex, String detailType) {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode forecastNode = rootNode.path(JsonPathsConstants.FORECAST)
                .path(JsonPathsConstants.FORECAST_DAY).get(dayIndex);

        boolean equalsTemp = detailType.equals(JsonPathsConstants.TEMP);
        if (equalsTemp) {
            return calculateAverageTemperature(forecastNode);
        } else {
            boolean equalsCondition = detailType.equals(JsonPathsConstants.CONDITION);
            if (equalsCondition) {
                return forecastNode.path(JsonPathsConstants.HOUR).get(0)
                        .path(JsonPathsConstants.CONDITION).path(JsonPathsConstants.TEXT).asText();
            } else {
                throw new IllegalArgumentException("Invalid detailType");
            }
        }
    }

    private String calculateAverageTemperature(JsonNode forecastNode) {
        double sumTemp = 0;
        int hoursCount = 0;
        for (JsonNode hourNode : forecastNode.path(JsonPathsConstants.HOUR)) {
            sumTemp += hourNode.path(JsonPathsConstants.TEMP_C).asDouble();
            hoursCount++;
        }

        if (hoursCount == 0) {
            throw new ArithmeticException();
        } else {
            return String.valueOf(sumTemp / hoursCount);
        }
    }

    private JsonNode findHourNode(Iterable<JsonNode> hoursNode, String hour) {
        for (JsonNode hourNode : hoursNode) {
            String text = hourNode.path(JsonPathsConstants.TIME).asText();
            if (text.contains(hour + ":00")) {
                return hourNode;
            }
        }
        throw new IllegalArgumentException("Hour not found");
    }
}
