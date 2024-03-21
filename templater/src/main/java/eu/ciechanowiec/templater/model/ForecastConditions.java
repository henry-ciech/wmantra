package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ForecastConditions {

    private String currentHourPlusTwoCondition;
    private String currentHourPlusFourCondition;
    private String currentHourPlusSixCondition;
    private String currentHourPlusEightCondition;
    private String currentHourPlusTenCondition;
    private String firstDayCondition;
    private String secondDayCondition;
}
