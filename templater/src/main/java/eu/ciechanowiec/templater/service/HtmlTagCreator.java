package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.HtmlData;
import eu.ciechanowiec.templater.model.WeatherCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HtmlTagCreator {

    private final JsonParser jsonParser;

    @Autowired
    public HtmlTagCreator(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public String createMainTag(String conditionFromRequest) {
        HtmlData htmlData = jsonParser.createHtmlData();
        Map<String, WeatherCondition> conditions = htmlData.getConditionMappings();

        WeatherCondition weatherCondition = conditions.get(conditionFromRequest);

        String tagTemplate = htmlData.getHtmlTemplate();
        String iconName = weatherCondition.getIconName();
        String iconColor = weatherCondition.getCustomColor();
        String size = htmlData.getMainIconSize();
        String marginTop = htmlData.getMainMarginTop();

        return processTemplate(tagTemplate, iconName, iconColor, size, marginTop);
    }

    public String createSubTag(String conditionFromRequest, String sizeFromJson) {
        HtmlData htmlData = jsonParser.createHtmlData();
        Map<String, WeatherCondition> conditions = htmlData.getConditionMappings();

        WeatherCondition weatherCondition = conditions.get(conditionFromRequest);
        String iconName = weatherCondition.getIconName();
        String iconColor = htmlData.getDefaultColor();
        String size;

        if (sizeFromJson.equals("big")) {
            size = htmlData.getBigIconSize();
        } else {
            size = htmlData.getSmallIconSize();
        }

        String tagTemplate = htmlData.getHtmlTemplate();
        String marginTop = htmlData.getSubMarginTop();

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
