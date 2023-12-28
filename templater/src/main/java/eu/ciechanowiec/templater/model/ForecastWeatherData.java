package eu.ciechanowiec.templater.model;

import lombok.ToString;

public record ForecastWeatherData(ForecastConditions forecastConditions, ForecastTemperatures forecastTemperatures) {

}
