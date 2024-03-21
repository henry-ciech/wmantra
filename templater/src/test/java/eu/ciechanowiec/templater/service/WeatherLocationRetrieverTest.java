package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherLocationRetrieverTest {

    private static final String PARTLY_CLOUDY = "Partly cloudy";
    private static final int HOUR = 21;
    private static final int MINUTE = 30;

    @SneakyThrows
    @Test
    void testRetrieve() {
        String filePath = "/home/debian/wmantra/templater/src/test/resources/jsonResponse.json";
        String jsonResponse = Files.readString(Paths.get(filePath));

        WeatherApiClient weatherApiClient = mock(WeatherApiClient.class);
        when(weatherApiClient.getResponse(anyDouble(), anyDouble())).thenReturn(jsonResponse);

        WeatherLocationRetriever weatherLocationRetriever = new WeatherLocationRetriever(weatherApiClient);
        WeatherData weatherData = weatherLocationRetriever.retrieve(0.0, 0.0);

        CurrentWeatherData currentWeatherData = weatherData.currentWeatherData();
        ForecastWeatherData forecastWeatherData = weatherData.forecastWeatherData();
        ForecastConditions forecastConditions = forecastWeatherData.forecastConditions();
        ForecastTemperatures forecastTemperatures = forecastWeatherData.forecastTemperatures();

        assertAll(
                () -> assertEquals(PARTLY_CLOUDY, currentWeatherData.currentCondition()),
                () -> assertEquals("Dagenham, United Kingdom", currentWeatherData.currentLocation()),
                () -> assertEquals("9", currentWeatherData.currentTemperature()),
                () -> assertEquals("WEDNESDAY", currentWeatherData.currentDay()),
                () -> assertEquals(LocalTime.of(HOUR, MINUTE), currentWeatherData.currentTime()),
                () -> assertEquals("Cloudy", forecastConditions.getCurrentHourPlusTwoCondition()),
                () -> assertEquals("Moderate rain", forecastConditions.getCurrentHourPlusFourCondition()),
                () -> assertEquals(PARTLY_CLOUDY, forecastConditions.getCurrentHourPlusSixCondition()),
                () -> assertEquals(PARTLY_CLOUDY, forecastConditions.getCurrentHourPlusEightCondition()),
                () -> assertEquals(PARTLY_CLOUDY, forecastConditions.getCurrentHourPlusTenCondition()),
                () -> assertEquals("Cloudy", forecastConditions.getFirstDayCondition()),
                () -> assertEquals("Overcast", forecastConditions.getSecondDayCondition()),
                () -> assertEquals("10.4", forecastTemperatures.getCurrentHourPlusTwoTemperature()),
                () -> assertEquals("8.2", forecastTemperatures.getCurrentHourPlusFourTemperature()),
                () -> assertEquals("8.7", forecastTemperatures.getCurrentHourPlusSixTemperature()),
                () -> assertEquals("8.3", forecastTemperatures.getCurrentHourPlusEightTemperature()),
                () -> assertEquals("8.1", forecastTemperatures.getCurrentHourPlusTenTemperature()),
                () -> assertEquals("10.545833333333333", forecastTemperatures.getFirstDayTemperature()),
                () -> assertEquals("8.333333333333332", forecastTemperatures.getSecondDayTemperature())
        );
    }
}
