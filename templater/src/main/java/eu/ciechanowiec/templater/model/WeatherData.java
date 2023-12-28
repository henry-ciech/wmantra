package eu.ciechanowiec.templater.model;

import lombok.ToString;

public record WeatherData(CurrentWeatherData currentWeatherData, ForecastWeatherData forecastWeatherData) {

}
