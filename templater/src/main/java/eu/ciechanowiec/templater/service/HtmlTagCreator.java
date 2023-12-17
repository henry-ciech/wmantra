package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.WeatherCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HtmlTagCreator {

    private final JsonParser jsonParser;
    private final String filePath;

    @Autowired
    public HtmlTagCreator(JsonParser jsonParser, @Value("${file.path}") String filePath) {
        this.filePath = filePath;
        this.jsonParser = jsonParser;
    }

    public String getMainTag(String conditionFromRequest) {
        Map<String, WeatherCondition> conditions = jsonParser.parseConditionsFromHtmlMap(filePath);
        Map<String, String> configData = jsonParser.parseConfigDataFromHtmlMap(filePath);

        WeatherCondition weatherCondition = conditions.get(conditionFromRequest);

        String tagTemplate = configData.get("htmlTemplate");
        String iconName = weatherCondition.getName();
        String iconColor = weatherCondition.getColor();
        String size = configData.get("mainIconSize");
        String marginTop = configData.get("mainMarginTop");

        return processTemplate(tagTemplate, iconName, iconColor, size, marginTop);
    }

    public String getSubTag(String conditionFromRequest, String sizeFromJson) {
        Map<String, WeatherCondition> conditions = jsonParser.parseConditionsFromHtmlMap(filePath);
        Map<String, String> constants = jsonParser.parseConfigDataFromHtmlMap(filePath);

        WeatherCondition weatherCondition = conditions.get(conditionFromRequest);
        String iconName = weatherCondition.getName();
        String iconColor = constants.get("defaultColor");
        String size;

        if (sizeFromJson.equals("big")) {
            size = constants.get("bigIconSize");
        } else {
            size = constants.get("smallIconSize");
        }

        String tagTemplate = constants.get("htmlTemplate");
        String marginTop = constants.get("subMarginTop");

        return processTemplate(tagTemplate, iconName, iconColor, size, marginTop);
    }

    private String processTemplate(String template, CharSequence iconName,
                                   CharSequence iconColor, CharSequence iconSize, CharSequence marginTop) {

        return template
                .replace("iconName", iconName)
                .replace("iconColor", iconColor)
                .replace("marginTop", marginTop)
                .replace("iconSize", iconSize);
    }
}
