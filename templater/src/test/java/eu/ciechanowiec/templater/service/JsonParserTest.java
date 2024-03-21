package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.HtmlData;
import eu.ciechanowiec.templater.model.WeatherCondition;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void testCreateHtmlData() {
        JsonParser jsonParser = new JsonParser();

        ReflectionTestUtils.setField(jsonParser, "jsonFilePath",
                "/home/debian/wmantra/templater/src/test/resources/map.json");

        HtmlData result = jsonParser.createHtmlData();

        Map<String, WeatherCondition> conditionMappings = result.getConditionMappings();
        WeatherCondition sunnyCondition = conditionMappings.get("sunny");
        WeatherCondition clearCondition = conditionMappings.get("clear");

        assertAll(
                () -> assertEquals("<i class=\"iconName\" style=\"color: iconColor; font-size: iconSize;"
                                + " margin-bottom: 8px; margin-top: marginTop; margin-right: 2x\"></i>",
                        result.getHtmlTemplate()),

                () -> assertNotNull(result),
                () -> assertEquals("#a9a9a9", result.getDefaultColor()),
                () -> assertEquals("40px", result.getSmallIconSize()),
                () -> assertEquals("55px", result.getBigIconSize()),
                () -> assertEquals("75px", result.getMainIconSize()),
                () -> assertEquals("17px", result.getMainMarginTop()),
                () -> assertEquals("0px", result.getSubMarginTop()),
                () -> assertNotNull(conditionMappings),
                () -> assertTrue(conditionMappings.containsKey("sunny")),
                () -> assertTrue(conditionMappings.containsKey("clear")),
                () -> assertNotNull(sunnyCondition),
                () -> assertEquals("fas fa-sun", sunnyCondition.getIconName()),
                () -> assertEquals("#ff8c00", sunnyCondition.getCustomColor()),
                () -> assertNotNull(clearCondition),
                () -> assertEquals("fas fa-moon", clearCondition.getIconName()),
                () -> assertEquals("#5f9ea0", clearCondition.getCustomColor())
        );
    }
}
