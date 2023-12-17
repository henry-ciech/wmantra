package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ForecastWeather {

    private final ConditionsForecast conditionsForecast;
    private final TemperaturesForecast temperaturesForecast;
}
