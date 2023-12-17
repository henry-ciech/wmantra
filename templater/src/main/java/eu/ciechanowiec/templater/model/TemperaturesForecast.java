package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TemperaturesForecast {

    private String currentHourPlusTwoTemperature;
    private String currentHourPlusFourTemperature;
    private String currentHourPlusSixTemperature;
    private String currentHourPlusEightTemperature;
    private String currentHourPlusTenTemperature;

    private String firstDayTemperature;
    private String secondDayTemperature;
}
