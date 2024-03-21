package eu.ciechanowiec.templater.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class WeatherCondition {

    private String iconName;
    private String customColor;
}
