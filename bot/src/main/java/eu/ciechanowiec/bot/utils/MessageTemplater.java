
package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.Location;
import eu.ciechanowiec.bot.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MessageTemplater {

    public static final String SHOW_CURRENT_WEATHER_TEXT = "🌥 Show current weather";
    public static final String CONFIGURE_TEXT = "⚙️ Configure";

    public String getGreetMessage(String userName) {
        return String.format(
                "Hello, %s! 👋%n"
                        + "Welcome to Weather Mantra Bot. It provides daily weather forecasts for "
                        + "your chosen location and time.%n"
                        + "You can also request the current weather forecast at any moment.%n",
                userName);
    }

    public String getWhenNextReports(Location location, User user) {
        LocalTime time = user.getTime();
        String timeStr = time.toString();

        return String.format(
                "⏰ Your daily weather updates are scheduled for *%s*%n🌍 Location: *%s, %s*%n",
                timeStr, location.city(), location.country());

    }

    public String getAskLocationMessage() {
        return """
                🌐 Please provide the location for which you'd like to get weather updates.
               Send it as a Telegram attachment.
                     """;
    }

    public String getWrongDataMessage() {
        return "I didn't understand that message 🤔 Please try again with a supported message type.";
    }

    public String getAskTimeMessage() {
        return """
                🕗 At what time do you prefer to receive daily weather updates?
                Please specify the time in this 24h format: HH:MM (e.g. 8:43, 13:51).
                """;
    }

    public String getErrorMessage() {
        return "Something went wrong, try later";
    }

    public String getAskConfigMessage() {
        return """
                ⚙️ You can modify location or time for your daily weather updates.

                🌐 To modify the *location* send the new one as a Telegram attachment.

                🕗 To modify the *time* send the new one in this 24h format: HH:MM (e.g. 8:43, 13:51).
                """;
    }

    public String getNotifyForNewLocation() {
        return "Understood! Your weather updates will now be for the new location.";
    }
}
