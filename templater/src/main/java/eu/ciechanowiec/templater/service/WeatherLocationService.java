package eu.ciechanowiec.templater.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ciechanowiec.templater.model.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherLocationService {

    private final RestTemplate restTemplate;
    private final ForecastTimeDayGetter forecastTimeDayGetter;
    private static final String API_KEY = "2bcfc05428d041a88e683012231211";
    private static final String TEMP_C = "temp_c";
    private static final String CONDITION = "condition";
    private static final String URL = "https://api.weatherapi.com/v1/forecast.json";
    private static final int BEGIN_INDEX_OF_TIME_IN_RESPONSE = 11;
    private static final int END_INDEX_OF_TIME_IN_RESPONSE = 16;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WeatherLocationService(RestTemplate restTemplate, ForecastTimeDayGetter forecastTimeDayGetter) {
        this.restTemplate = restTemplate;
        this.forecastTimeDayGetter = forecastTimeDayGetter;
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    public WeatherData getWeather(double longitude, double latitude) throws JsonProcessingException {
        String url = getWeatherUrl(latitude, longitude);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String responseBody = response.getBody();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode locationNode = rootNode.path("location");
        JsonNode currentWeatherNode = rootNode.path("current");
        String tempC = currentWeatherNode.path(TEMP_C).asText();
        String condition = currentWeatherNode.path(CONDITION).path("text").asText();
        String dateAndTime = currentWeatherNode.path("last_updated").asText();

        String timeStr = dateAndTime.substring(BEGIN_INDEX_OF_TIME_IN_RESPONSE, END_INDEX_OF_TIME_IN_RESPONSE);
        LocalTime time = LocalTime.parse(timeStr);

        String city = locationNode.path("name").asText();
        String country = locationNode.path("country").asText();
        String location = city + ", " + country;
        String currentDay = getDayOfWeek(rootNode);

        CurrentWeatherData currentWeatherData = new CurrentWeatherData(condition, currentDay, location, tempC, time);
        TemperaturesForecast temperaturesForecast = getTemperaturesForecast(responseBody, time);
        ConditionsForecast conditionsForecast = getConditionsForecast(responseBody, time);
        ForecastWeather forecastWeather = new ForecastWeather(conditionsForecast, temperaturesForecast);

        return new WeatherData(currentWeatherData, forecastWeather);
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static String getDayOfWeek(JsonNode rootNode) {
        String localtime = rootNode.path("location").path("localtime").asText();
        LocalDate date = LocalDate.parse(localtime, DateTimeFormatter.ofPattern("yyyy-MM-dd [H][HH]:mm"));
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.toString();
    }

    private TemperaturesForecast getTemperaturesForecast(String jsonResponse, LocalTime timeStamp) {
        int[] upcomingHours = forecastTimeDayGetter.getNextTenHours(timeStamp);
        int day = 0;
        String[] hourByHourForecast = new String[5];

        for (int i = 0; i < upcomingHours.length; i++) {
            if (upcomingHours[i] <= 2) {
                day++;
            }
            hourByHourForecast[i] = getTemperature(jsonResponse, upcomingHours[i], day);
        }

        String firstDayTemperature = getWeatherDetailByDay(jsonResponse, 1, "temp");
        String secondDayTemperature = getWeatherDetailByDay(jsonResponse, 2, "temp");

        return new TemperaturesForecast(hourByHourForecast[0], hourByHourForecast[1], hourByHourForecast[2],
                hourByHourForecast[3], hourByHourForecast[4],
                firstDayTemperature, secondDayTemperature);
    }

    private ConditionsForecast getConditionsForecast(String jsonResponse, LocalTime timeStamp) {
        int[] upcomingHours = forecastTimeDayGetter.getNextTenHours(timeStamp);
        int day = 0;
        String[] hourByHourForecast = new String[5];

        for (int i = 0; i < upcomingHours.length; i++) {
            if (upcomingHours[i] <= 2) {
                day++;
            }
            String upcomingHoursStr = Integer.toString(upcomingHours[i]);
            hourByHourForecast[i] = getCondition(jsonResponse,  upcomingHoursStr, day);
        }

        String secondDayCondition = getWeatherDetailByDay(jsonResponse, 1, CONDITION);
        String thirdDayCondition = getWeatherDetailByDay(jsonResponse, 2, CONDITION);

        return new ConditionsForecast(hourByHourForecast[0], hourByHourForecast[1], hourByHourForecast[2],
                hourByHourForecast[3], hourByHourForecast[4],
                secondDayCondition, thirdDayCondition);
    }

    @SneakyThrows
    private String getTemperature(String jsonResponse, int hourToFind, int dayIndexToFind) {
        String hourStr = String.valueOf(hourToFind);
        JsonNode hourNode = getHourNode(jsonResponse, hourStr, dayIndexToFind);
        return hourNode.path(TEMP_C).asText();
    }

    @SneakyThrows
    private String getCondition(String jsonResponse, String hourToFind, int dayIndexToFind) {
        JsonNode hourNode = getHourNode(jsonResponse, hourToFind, dayIndexToFind);
        return hourNode.path(CONDITION).path("text").asText();
    }

    @SneakyThrows
    private JsonNode getHourNode(String jsonResponse, String hourToFins, int dayIndexToFind) {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode forecastNode = rootNode.path("forecast").path("forecastday").get(dayIndexToFind);
        return findHourNode(forecastNode.path("hour"), hourToFins);
    }


    @SneakyThrows
    private String getWeatherDetailByDay(String jsonResponse, int dayIndex, String detailType) {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode forecastNode = rootNode.path("forecast").path("forecastday").get(dayIndex);

        if ("temp".equals(detailType)) {
            return calculateAverageTemperature(forecastNode);
        } else if (CONDITION.equals(detailType)) {
            return forecastNode.path("hour").get(0).path(CONDITION).path("text").asText();
        } else {
            throw new IllegalArgumentException("Invalid detailType");
        }
    }

    private String calculateAverageTemperature(JsonNode forecastNode) {
        double sumTemp = 0;
        int hoursCount = 0;
        for (JsonNode hourNode : forecastNode.path("hour")) {
            sumTemp += hourNode.path(TEMP_C).asDouble();
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
            String text = hourNode.path("time").asText();
            if (text.contains(hour + ":00")) {
                return hourNode;
            }
        }
        throw new IllegalArgumentException("Hour not found");
    }

    @SuppressWarnings("SameParameterValue")
    private String getWeatherUrl(double latitude, double longitude) {
        UriComponentsBuilder currentBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("key", API_KEY)
                .queryParam("q", latitude + "," + longitude)
                .queryParam("days", 3);

        return currentBuilder.toUriString();
    }
}
