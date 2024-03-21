package eu.ciechanowiec.templater.controller;

import eu.ciechanowiec.templater.service.HtmlTagCreator;
import eu.ciechanowiec.templater.service.JsonParser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.spy;

@TestConfiguration
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestConfig {

    @Bean
    public HtmlTagCreator htmlTagCreator() {
        JsonParser jsonParser = spy(new JsonParser());
        ReflectionTestUtils.setField(jsonParser, "jsonFilePath",
                "/home/debian/wmantra/templater/src/test/resources/map.json");
        return new HtmlTagCreator(jsonParser);
    }
}
