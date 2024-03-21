package eu.ciechanowiec.templater.controller;

import eu.ciechanowiec.templater.model.*;
import eu.ciechanowiec.templater.service.WeatherLocationRetriever;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.result.ViewResultMatchers;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
@Import(TestConfig.class)
@SuppressWarnings("PMD.TooManyStaticImports")
class MainControllerTest {

    private static final String CLOUD_TEMPLATE = "<i class=\"fas fa-cloud\" style=\"color: #a9a9a9; font-size:"
            + " 40px; margin-bottom: 8px; margin-top: 0px; margin-right: 2x\"></i>";
    private static final String PARTLY_CLOUDY = "Partly cloudy";
    private static final int HOUR = 21;
    private static final int MINUTE = 30;
    private static final String TEMPERATURE = "8.3";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherLocationRetriever weatherLocationRetriever;

    @SneakyThrows
    @Test
    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    void modelShouldHaveCorrectData() {
        String currentPlusFourIcon = "<i class=\"fas fa-cloud-rain\" style=\"color: #a9a9a9; font-size: "
                + "40px; margin-bottom: 8px; margin-top: 0px; margin-right: 2x\"></i>";
        String expectedFirstIcon = "<i class=\"fas fa-cloud\" style=\"color: #a9a9a9; font-size: 55px; "
                + "margin-bottom: 8px; margin-top: 0px; margin-right: 2x\"></i>";
        String expectedSecondIcon = "<i class=\"fas fa-cloud\" style=\"color: #a9a9a9; font-size: 55px; "
                + "margin-bottom: 8px; margin-top: 0px; margin-right: 2x\"></i>";
        String expectedCurrentIcon = "<i class=\"fas fa-cloud\" style=\"color: #737373; font-size: 75px; "
                + "margin-bottom: 8px; margin-top: 17px; margin-right: 2x\"></i>";
        WeatherData expectedWeatherData = getTestData();
        when(weatherLocationRetriever.retrieve(anyDouble(), anyDouble())).thenReturn(expectedWeatherData);

        ModelResultMatchers model = model();
        StatusResultMatchers status = status();
        ViewResultMatchers view = view();
        MvcResult mvcResult = mockMvc.perform(get("/")
                        .param("longitude", String.valueOf(0.0))
                        .param("latitude", String.valueOf(0.0)))
                .andExpect(status.isOk())
                .andExpect(view.name("index"))
                .andExpect(model.attribute("currentHourPlusTwoIcon", CLOUD_TEMPLATE))
                .andExpect(model.attribute("currentHourPlusFourIcon", currentPlusFourIcon))
                .andExpect(model.attribute("currentHourPlusSixIcon", CLOUD_TEMPLATE))
                .andExpect(model.attribute("currentHourPlusEightIcon", CLOUD_TEMPLATE))
                .andExpect(model.attribute("currentHourPlusTenIcon", CLOUD_TEMPLATE))
                .andExpect(model.attribute("firstIcon", expectedFirstIcon))
                .andExpect(model.attribute("secondIcon", expectedSecondIcon))
                .andExpect(model.attribute("currentLocation", "Dagenham, United Kingdom"))
                .andExpect(model.attribute("currentTemperature", "9"))
                .andExpect(model.attribute("currentIconText", PARTLY_CLOUDY))
                .andExpect(model.attribute("currentIcon", expectedCurrentIcon))
                .andExpect(model.attribute("currentHourPlusTwo", 0))
                .andExpect(model.attribute("currentHourPlusFour", 2))
                .andExpect(model.attribute("currentHourPlusSix", 4))
                .andExpect(model.attribute("currentHourPlusEight", 6))
                .andExpect(model.attribute("currentHourPlusTen", 8))
                .andExpect(model.attribute("currentHourPlusTwoTemperature", "10.4"))
                .andExpect(model.attribute("currentHourPlusFourTemperature", "8.2"))
                .andExpect(model.attribute("currentHourPlusSixTemperature", "8.7"))
                .andExpect(model.attribute("currentHourPlusEightTemperature", TEMPERATURE))
                .andExpect(model.attribute("currentPlusTenTemperature", "8.1"))
                .andExpect(model.attribute("firstDayTemperature", "10.5"))
                .andExpect(model.attribute("secondDayTemperature", TEMPERATURE))
                .andExpect(model.attribute("firstDayName", "Thursday"))
                .andExpect(model.attribute("secondDayName", "Friday"))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
    }

    WeatherData getTestData() {
        ForecastConditions forecastConditions = new ForecastConditions("Cloudy",
                "Moderate rain", PARTLY_CLOUDY,
                PARTLY_CLOUDY, PARTLY_CLOUDY,
                "Cloudy", "Overcast");

        ForecastTemperatures forecastTemperatures =
                new ForecastTemperatures("10.4", "8.2",
                        "8.7", TEMPERATURE,
                        "8.1", "10.545833333333333",
                        "8.333333333333332");

        ForecastWeatherData forecastWeatherData = new ForecastWeatherData(forecastConditions, forecastTemperatures);

        CurrentWeatherData currentWeatherData = new CurrentWeatherData(PARTLY_CLOUDY,
                "WEDNESDAY", "Dagenham, United Kingdom", "9",
                LocalTime.of(HOUR, MINUTE));

        return new WeatherData(currentWeatherData, forecastWeatherData);
    }
}
