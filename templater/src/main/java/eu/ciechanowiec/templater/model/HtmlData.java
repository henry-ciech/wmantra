package eu.ciechanowiec.templater.model;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
public class HtmlData {

    private String htmlTemplate;
    private String defaultColor;
    private String smallIconSize;
    private String bigIconSize;
    private String mainIconSize;
    private String mainMarginTop;
    private String subMarginTop;
    private Map<String, WeatherCondition> conditionMappings;
}
