package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@SuppressWarnings("PMD.LongVariable")
public class ForecastTemperatures {

    private String currentHourPlusTwoTemperature;
    private String currentHourPlusFourTemperature;
    private String currentHourPlusSixTemperature;
    private String currentHourPlusEightTemperature;
    private String currentHourPlusTenTemperature;
    private String firstDayTemperature;
    private String secondDayTemperature;
}
