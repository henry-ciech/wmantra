package eu.ciechanowiec.templater.model;

import lombok.Data;

@SuppressWarnings("ClassCanBeRecord")
@Data
public class WeatherCondition {

    private final String name;
    private final String color;
}
