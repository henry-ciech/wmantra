package eu.ciechanowiec.templater.controller;

import eu.ciechanowiec.templater.model.*;
import eu.ciechanowiec.templater.service.UpcomingTimeAndDayCalculator;
import eu.ciechanowiec.templater.service.HtmlTagCreator;
import eu.ciechanowiec.templater.service.WeatherLocationRetriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;

@Controller
@Slf4j
public class MainController implements ErrorController {

    private static final String SMALL_SIZE = "small";
    private static final String BIG_SIZE = "big";
    private static final String ERROR_HTML = "error";
    private final HtmlTagCreator htmlTagCreator;
    private final UpcomingTimeAndDayCalculator upcomingTimeAndDayCalculator;
    private final WeatherLocationRetriever weatherLocationRetriever;

    @Autowired
    MainController(WeatherLocationRetriever weatherLocationRetriever, HtmlTagCreator htmlTagCreator) {
        upcomingTimeAndDayCalculator = new UpcomingTimeAndDayCalculator();
        this.htmlTagCreator = htmlTagCreator;
        this.weatherLocationRetriever = weatherLocationRetriever;
    }

    @RequestMapping("/error")
    public ModelAndView handleError() {
        return new ModelAndView(ERROR_HTML);
    }

    @GetMapping("/")
    @SuppressWarnings({
            "java:S2221", "CatchAndPrintStackTrace", "OverlyBroadCatchBlock",
            "PMD.AvoidCatchingGenericException", "IllegalCatch", "Indentation"
    })
    public String index(@RequestParam double longitude, @RequestParam double latitude, Model model) {
        log.info("Got a request, latitude: {}, longitude: {}", latitude, longitude);
        try {
            WeatherData weatherData = weatherLocationRetriever.retrieve(longitude, latitude);

            addIconsData(model, weatherData);
            addCurrentData(model, weatherData);
            addNextHoursData(model, weatherData);
            addNextDaysData(model, weatherData);
            log.info("Exposing index, waiting for a screenshot");

            return "index";
        } catch (Exception exception) {
            log.error("Service unavailable due to an exception", exception);
            return ERROR_HTML;
        }
    }

    private void addIconsData(Model model, WeatherData weatherData) {
        ForecastWeatherData forecastWeatherData = weatherData.forecastWeatherData();
        ForecastConditions forecastConditions = forecastWeatherData.forecastConditions();

        String currentHourPlusTwoIcon =
                htmlTagCreator.createSubTag(forecastConditions.getCurrentHourPlusTwoCondition(), SMALL_SIZE);
        String currentHourPlusFourIcon =
                htmlTagCreator.createSubTag(forecastConditions.getCurrentHourPlusFourCondition(), SMALL_SIZE);
        String currentHourPlusSixIcon =
                htmlTagCreator.createSubTag(forecastConditions.getCurrentHourPlusSixCondition(), SMALL_SIZE);
        String currentHourPlusEightIcon =
                htmlTagCreator.createSubTag(forecastConditions.getCurrentHourPlusEightCondition(), SMALL_SIZE);
        String currentHourPlusTenIcon =
                htmlTagCreator.createSubTag(forecastConditions.getCurrentHourPlusTenCondition(), SMALL_SIZE);

        String firstDayIcon = htmlTagCreator.createSubTag(forecastConditions.getFirstDayCondition(), BIG_SIZE);
        String secondDayIcon = htmlTagCreator.createSubTag(forecastConditions.getSecondDayCondition(), BIG_SIZE);

        model.addAttribute("currentHourPlusTwoIcon", currentHourPlusTwoIcon);
        model.addAttribute("currentHourPlusFourIcon", currentHourPlusFourIcon);
        model.addAttribute("currentHourPlusSixIcon", currentHourPlusSixIcon);
        model.addAttribute("currentHourPlusEightIcon", currentHourPlusEightIcon);
        model.addAttribute("currentHourPlusTenIcon", currentHourPlusTenIcon);

        model.addAttribute("firstIcon", firstDayIcon);
        model.addAttribute("secondIcon", secondDayIcon);
    }

    private void addCurrentData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.currentWeatherData();
        String currentDayIcon = htmlTagCreator.createMainTag(currentWeatherData.currentCondition());

        model.addAttribute("currentLocation", currentWeatherData.currentLocation());
        model.addAttribute("currentTemperature", currentWeatherData.currentTemperature());
        model.addAttribute("currentIconText", currentWeatherData.currentCondition());
        model.addAttribute("currentIcon", currentDayIcon);
    }

    private void addNextDaysData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.currentWeatherData();

        String currentDay = currentWeatherData.currentDay();
        DayOfWeek day = DayOfWeek.valueOf(currentDay.toUpperCase(Locale.ROOT));
        String[] days = upcomingTimeAndDayCalculator.calculateNextTwoDays(day);
        ForecastWeatherData forecastWeatherData = weatherData.forecastWeatherData();
        ForecastTemperatures forecastTemperatures = forecastWeatherData.forecastTemperatures();

        String firstDayTemperature = forecastTemperatures.getFirstDayTemperature();
        String secondDayTemperature = forecastTemperatures.getSecondDayTemperature();

        double firstDayTemp = Double.parseDouble(firstDayTemperature);
        double secondDayTemp = Double.parseDouble(secondDayTemperature);

        String formattedFirstDayTemp = String.format("%.1f", firstDayTemp);
        String formattedSecondDayTemp = String.format("%.1f", secondDayTemp);

        model.addAttribute("firstDayTemperature", formattedFirstDayTemp);
        model.addAttribute("secondDayTemperature", formattedSecondDayTemp);
        model.addAttribute("firstDayName", days[0]);
        model.addAttribute("secondDayName", days[1]);
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private void addNextHoursData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.currentWeatherData();
        LocalTime currentTime = currentWeatherData.currentTime();

        int[] hours = upcomingTimeAndDayCalculator.calculateNextTenHours(currentTime);
        ForecastWeatherData forecastWeatherData = weatherData.forecastWeatherData();
        ForecastTemperatures forecastTemperatures = forecastWeatherData.forecastTemperatures();

        model.addAttribute("currentHourPlusTwo", hours[0]);
        model.addAttribute("currentHourPlusFour", hours[1]);
        model.addAttribute("currentHourPlusSix", hours[2]);
        model.addAttribute("currentHourPlusEight", hours[3]);
        model.addAttribute("currentHourPlusTen", hours[4]);
        model.addAttribute("currentHourPlusTwoTemperature",
                forecastTemperatures.getCurrentHourPlusTwoTemperature());
        model.addAttribute("currentHourPlusFourTemperature",
                forecastTemperatures.getCurrentHourPlusFourTemperature());
        model.addAttribute("currentHourPlusSixTemperature",
                forecastTemperatures.getCurrentHourPlusSixTemperature());
        model.addAttribute("currentHourPlusEightTemperature",
                forecastTemperatures.getCurrentHourPlusEightTemperature());
        model.addAttribute("currentPlusTenTemperature",
                forecastTemperatures.getCurrentHourPlusTenTemperature());
    }
}
