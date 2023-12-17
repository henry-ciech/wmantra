package eu.ciechanowiec.templater.controller;

import eu.ciechanowiec.templater.model.*;
import eu.ciechanowiec.templater.service.ForecastTimeDayGetter;
import eu.ciechanowiec.templater.service.HtmlTagCreator;
import eu.ciechanowiec.templater.service.WeatherLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;

@Controller
@Slf4j
public class MainController {

    private final HtmlTagCreator htmlTagCreator;
    private final ForecastTimeDayGetter forecastTimeDayGetter;
    private final WeatherLocationService weatherLocationService;
    private static final String SMALL_SIZE = "small";
    private static final String BIG_SIZE = "big";

    @Autowired
    MainController(HtmlTagCreator htmlTagCreator, ForecastTimeDayGetter forecastTimeDayGetter,
                   WeatherLocationService weatherLocationService) {
        this.forecastTimeDayGetter = forecastTimeDayGetter;
        this.htmlTagCreator = htmlTagCreator;
        this.weatherLocationService = weatherLocationService;
    }

    @SuppressWarnings({"java:S2221", "CatchAndPrintStackTrace", "OverlyBroadCatchBlock"})
    @GetMapping("/")
    public String index(@RequestParam double longitude, @RequestParam double latitude, Model model) {
        try {
            WeatherData weatherData = weatherLocationService.getWeather(longitude, latitude);

            addIconsData(model, weatherData);
            addCurrentData(model, weatherData);
            addNextHoursData(model, weatherData);
            addNextDaysData(model, weatherData);

            return "index";
        } catch (Exception exception) {
            log.error("Service unavailable due to an exception", exception);
            return "error";
        }
    }

    private void addIconsData(Model model, WeatherData weatherData) {
        ForecastWeather forecastWeather = weatherData.getForecastWeather();
        ConditionsForecast conditionsForecast = forecastWeather.getConditionsForecast();

        String currentHourPlusTwoIcon =
                htmlTagCreator.getSubTag(conditionsForecast.getCurrentHourPlusTwoCondition(), SMALL_SIZE);
        String currentHourPlusFourIcon =
                htmlTagCreator.getSubTag(conditionsForecast.getCurrentHourPlusFourCondition(), SMALL_SIZE);
        String currentHourPlusSixIcon =
                htmlTagCreator.getSubTag(conditionsForecast.getCurrentHourPlusSixCondition(), SMALL_SIZE);
        String currentHourPlusEightIcon =
                htmlTagCreator.getSubTag(conditionsForecast.getCurrentHourPlusEightCondition(), SMALL_SIZE);
        String currentHourPlusTenIcon =
                htmlTagCreator.getSubTag(conditionsForecast.getCurrentHourPlusTenCondition(), SMALL_SIZE);

        String firstDayIcon = htmlTagCreator.getSubTag(conditionsForecast.getFirstDayCondition(), BIG_SIZE);
        String secondDayIcon = htmlTagCreator.getSubTag(conditionsForecast.getSecondDayCondition(), BIG_SIZE);

        model.addAttribute("currentHourPlusTwoIcon", currentHourPlusTwoIcon);
        model.addAttribute("currentHourPlusFourIcon", currentHourPlusFourIcon);
        model.addAttribute("currentHourPlusSixIcon", currentHourPlusSixIcon);
        model.addAttribute("currentHourPlusEightIcon", currentHourPlusEightIcon);
        model.addAttribute("currentHourPlusTenIcon", currentHourPlusTenIcon);

        model.addAttribute("firstIcon", firstDayIcon);
        model.addAttribute("secondIcon", secondDayIcon);
    }

    private void addCurrentData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.getCurrentWeatherData();
        String currentDayIcon = htmlTagCreator.getMainTag(currentWeatherData.getCurrentCondition());

        model.addAttribute("currentLocation", currentWeatherData.getCurrentLocation());
        model.addAttribute("currentTemperature", currentWeatherData.getCurrentTemperature());
        model.addAttribute("currentIconText", currentWeatherData.getCurrentCondition());
        model.addAttribute("currentIcon", currentDayIcon);
    }

    private void addNextDaysData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.getCurrentWeatherData();

        String currentDay = currentWeatherData.getCurrentDay();
        String[] days = forecastTimeDayGetter.getNextTwoDays(currentDay);
        ForecastWeather forecastWeather = weatherData.getForecastWeather();
        TemperaturesForecast temperaturesForecast = forecastWeather.getTemperaturesForecast();

        String firstDayTemperature = temperaturesForecast.getFirstDayTemperature();
        String secondDayTemperature = temperaturesForecast.getSecondDayTemperature();

        double firstDayTemp = Double.parseDouble(firstDayTemperature);
        double secondDayTemp = Double.parseDouble(secondDayTemperature);

        String formattedFirstDayTemp = String.format("%.1f", firstDayTemp);
        String formattedSecondDayTemp = String.format("%.1f", secondDayTemp);

        model.addAttribute("firstDayTemperature", formattedFirstDayTemp);
        model.addAttribute("secondDayTemperature", formattedSecondDayTemp);
        model.addAttribute("firstDayName", days[0]);
        model.addAttribute("secondDayName", days[1]);
    }

    private void addNextHoursData(Model model, WeatherData weatherData) {
        CurrentWeatherData currentWeatherData = weatherData.getCurrentWeatherData();
        LocalTime currentTime = currentWeatherData.getCurrentTime();

        int[] hours = forecastTimeDayGetter.getNextTenHours(currentTime);
        ForecastWeather forecastWeather = weatherData.getForecastWeather();
        TemperaturesForecast temperaturesForecast = forecastWeather.getTemperaturesForecast();

        model.addAttribute("currentHourPlusTwo", hours[0]);
        model.addAttribute("currentHourPlusFour", hours[1]);
        model.addAttribute("currentHourPlusSix", hours[2]);
        model.addAttribute("currentHourPlusEight", hours[3]);
        model.addAttribute("currentHourPlusTen", hours[4]);
        model.addAttribute("currentHourPlusTwoTemperature", temperaturesForecast.getCurrentHourPlusTwoTemperature());
        model.addAttribute("currentHourPlusFourTemperature", temperaturesForecast.getCurrentHourPlusFourTemperature());
        model.addAttribute("currentHourPlusSixTemperature", temperaturesForecast.getCurrentHourPlusSixTemperature());
        model.addAttribute("currentHourPlusEightTemperature", temperaturesForecast.getCurrentHourPlusEightTemperature());
        model.addAttribute("currentPlusTenTemperature", temperaturesForecast.getCurrentHourPlusTenTemperature());
    }
}
