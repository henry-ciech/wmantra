package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.HtmlData;
import eu.ciechanowiec.templater.model.TemplateInfo;
import eu.ciechanowiec.templater.model.WeatherCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class HtmlTagCreator {

    private static final String SIZE_BIG = "big";
    private final JsonParser jsonParser;

    @Autowired
    public HtmlTagCreator(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public String createMainTag(String conditionFromRequest) {
        String trimmedCondition = conditionFromRequest.trim();
        String normalizedCondition = trimmedCondition.toLowerCase(Locale.ROOT);
        HtmlData htmlData = jsonParser.createHtmlData();
        Map<String, WeatherCondition> conditions = htmlData.getConditionMappings();
        extendConditionsMap(conditions);

        Optional<WeatherCondition> weatherConditionNullable = Optional.ofNullable(conditions.get(normalizedCondition));

        String tagTemplate = htmlData.getHtmlTemplate();
        String size = htmlData.getMainIconSize();
        String marginTop = htmlData.getMainMarginTop();

        String iconName;
        String iconColor;

        if (weatherConditionNullable.isEmpty()) {
            WeatherCondition defaultIcon = conditions.entrySet().stream()
                    .filter(entry -> normalizedCondition.contains(entry.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(new WeatherCondition("fas fa-question", "#737373")); // Default icon and color

            iconName = defaultIcon.getIconName();
            iconColor = defaultIcon.getCustomColor();
        } else {
            WeatherCondition weatherCondition = weatherConditionNullable.get();
            iconName = weatherCondition.getIconName();
            iconColor = weatherCondition.getCustomColor();
        }

        TemplateInfo templateInfo = new TemplateInfo(iconName, iconColor, size, marginTop);

        return processTemplate(tagTemplate, templateInfo);
    }

    private void extendConditionsMap(Map<String, WeatherCondition> conditions) {
        conditions.put("cloud", new WeatherCondition("fas fa-cloud", "#737373"));
        conditions.put("rain", new WeatherCondition("fas fa-cloud-rain", "#0099cc"));
        conditions.put("snow", new WeatherCondition("fas fa-snowflake", "#00bfff"));
        conditions.put("sleet", new WeatherCondition("fas fa-snowflake", "#00bfff"));
        conditions.put("drizzle", new WeatherCondition("fas fa-cloud-showers-heavy", "#0099cc"));
        conditions.put("thunder", new WeatherCondition("fas fa-bolt", "#ffcc00"));
        conditions.put("shower", new WeatherCondition("fas fa-cloud-rain", "#4682b4"));
    }

    public String createSubTag(String conditionFromRequest, String sizeFromJson) {
        String trimmedCondition = conditionFromRequest.trim();
        String normalizedCondition = trimmedCondition.toLowerCase(Locale.ROOT);
        HtmlData htmlData = jsonParser.createHtmlData();
        Map<String, WeatherCondition> conditions = htmlData.getConditionMappings();
        extendConditionsMap(conditions);
        Optional<WeatherCondition> weatherConditionNullable = Optional.ofNullable(conditions.get(normalizedCondition));

        String iconName;
        String iconColor = htmlData.getDefaultColor();
        String size;

        if (weatherConditionNullable.isEmpty()) {
            WeatherCondition defaultIcon = conditions.entrySet().stream()
                    .filter(entry -> normalizedCondition.contains(entry.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(new WeatherCondition("fas fa-question", iconColor)); // Default icon and color

            iconName = defaultIcon.getIconName();
        } else {
            WeatherCondition weatherCondition = weatherConditionNullable.get();
            iconName = weatherCondition.getIconName();
        }

        if (sizeFromJson.equals(SIZE_BIG)) {
            size = htmlData.getBigIconSize();
        } else {
            size = htmlData.getSmallIconSize();
        }

        String tagTemplate = htmlData.getHtmlTemplate();
        String marginTop = htmlData.getSubMarginTop();
        TemplateInfo templateInfo = new TemplateInfo(iconName, iconColor, size, marginTop);

        return processTemplate(tagTemplate, templateInfo);
    }

    private String processTemplate(String template, TemplateInfo templateInfo) {

        String iconName = templateInfo.iconName();
        String iconColor = templateInfo.iconColor();
        String marginTop = templateInfo.marginTop();
        String iconSize = templateInfo.iconSize();
        return template
                .replace("iconName", iconName)
                .replace("iconColor", iconColor)
                .replace("marginTop", marginTop)
                .replace("iconSize", iconSize);
    }
}
