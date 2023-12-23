package eu.ciechanowiec.templater.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class WeatherCondition {

    private String iconName;
    private String customColor;
}
