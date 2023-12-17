package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@Getter
@AllArgsConstructor
public class WeatherData {

    private final CurrentWeatherData currentWeatherData;
    private final ForecastWeather forecastWeather;
}
