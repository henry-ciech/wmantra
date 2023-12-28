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

    @SneakyThrows
    @Test
    void testRetrieve() {
        String filePath = "/home/henryk/0_prog/wmantra/templater/src/test/resources/jsonResponse.json";
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
                () -> assertEquals("Partly cloudy", currentWeatherData.currentCondition()),
                () -> assertEquals("Dagenham, United Kingdom", currentWeatherData.currentLocation()),
                () -> assertEquals("9", currentWeatherData.currentTemperature()),
                () -> assertEquals("WEDNESDAY", currentWeatherData.currentDay()),
                () -> assertEquals(LocalTime.of(21, 30), currentWeatherData.currentTime()),
                () -> assertEquals("Cloudy", forecastConditions.getCurrentHourPlusTwoCondition()),
                () -> assertEquals("Moderate rain", forecastConditions.getCurrentHourPlusFourCondition()),
                () -> assertEquals("Partly cloudy", forecastConditions.getCurrentHourPlusSixCondition()),
                () -> assertEquals("Partly cloudy", forecastConditions.getCurrentHourPlusEightCondition()),
                () -> assertEquals("Partly cloudy", forecastConditions.getCurrentHourPlusTenCondition()),
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
