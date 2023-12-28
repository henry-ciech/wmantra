package eu.ciechanowiec.templater.model;

import lombok.ToString;

import java.time.LocalTime;

public record CurrentWeatherData(String currentCondition, String currentDay, String currentLocation,
                                 String currentTemperature, LocalTime currentTime) {

}
