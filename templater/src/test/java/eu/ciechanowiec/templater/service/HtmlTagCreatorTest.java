package eu.ciechanowiec.templater.service;

import eu.ciechanowiec.templater.model.HtmlData;
import eu.ciechanowiec.templater.model.WeatherCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class HtmlTagCreatorTest {

    private static final String SUNNY = "sunny";
    @Mock
    private JsonParser jsonParser;

    @InjectMocks
    private HtmlTagCreator htmlTagCreator;

    private HtmlData testHtmlData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpTestHtmlData();
        when(jsonParser.createHtmlData()).thenReturn(testHtmlData);
    }

    private void setUpTestHtmlData() {
        testHtmlData = new HtmlData();

        ReflectionTestUtils.setField(testHtmlData, "htmlTemplate", "<i class=\"iconName\" style=\"color: "
                + "iconColor; font-size: iconSize; margin-bottom: 8px; margin-top: marginTop; margin-right: 2x\"></i>");
        ReflectionTestUtils.setField(testHtmlData, "defaultColor", "#a9a9a9");
        ReflectionTestUtils.setField(testHtmlData, "smallIconSize", "40px");
        ReflectionTestUtils.setField(testHtmlData, "bigIconSize", "55px");
        ReflectionTestUtils.setField(testHtmlData, "mainIconSize", "75px");
        ReflectionTestUtils.setField(testHtmlData, "mainMarginTop", "17px");
        ReflectionTestUtils.setField(testHtmlData, "subMarginTop", "0px");

        Map<String, WeatherCondition> conditionMappings = new ConcurrentHashMap<>();
        conditionMappings.put(SUNNY, createWeatherCondition("fas fa-sun", "#ff8c00"));
        conditionMappings.put("clear", createWeatherCondition("fas fa-moon", "#5f9ea0"));
        ReflectionTestUtils.setField(testHtmlData, "conditionMappings", conditionMappings);
    }

    private WeatherCondition createWeatherCondition(String iconName, String customColor) {
        WeatherCondition condition = new WeatherCondition();
        ReflectionTestUtils.setField(condition, "iconName", iconName);
        ReflectionTestUtils.setField(condition, "customColor", customColor);
        return condition;
    }

    @Test
    void testCreateMainTag() {
        String expected = "<i class=\"fas fa-sun\" style=\"color: #ff8c00; font-size: 75px; margin-bottom: 8px;"
                + " margin-top: 17px; margin-right: 2x\"></i>";
        String result = htmlTagCreator.createMainTag(SUNNY);

        assertEquals(expected, result);
    }

    @Test
    void testCreateSubTag() {
        String expectedBig = "<i class=\"fas fa-sun\" style=\"color: #a9a9a9; font-size: 55px; margin-bottom: "
                + "8px; margin-top: 0px; margin-right: 2x\"></i>";
        String resultBig = htmlTagCreator.createSubTag(SUNNY, "big");

        assertEquals(expectedBig, resultBig);

        String expectedSmall = "<i class=\"fas fa-sun\" style=\"color: #a9a9a9; font-size: 40px; margin-bottom: "
                + "8px; margin-top: 0px; margin-right: 2x\"></i>";
        String resultSmall = htmlTagCreator.createSubTag(SUNNY, "small");

        assertEquals(expectedSmall, resultSmall);
    }
}
