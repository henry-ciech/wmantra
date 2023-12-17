package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@SuppressWarnings("ClassCanBeRecord")
@AllArgsConstructor
@Data
public class CurrentWeatherData {

    private final String currentCondition;
    private final String currentDay;
    private final String currentLocation;
    private final String currentTemperature;
    private final LocalTime currentTime;

}
