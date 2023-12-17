package eu.ciechanowiec.bot.model;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum Command {

    START(List.of("/start"), true),
    ASK_LOCATION(List.of("/askLocation"), false),
    ASK_TIME(List.of("/askTime"), false),
    CONFIG(List.of("/config", "⚙️ Configure"), true),
    UNKNOWN(List.of("/unknown"), false),
    SAVE_LOCATION(List.of("/saveLocation"), false),
    SAVE_TIME(List.of("/saveTime"), false),
    SHOW_CURRENT_SETTINGS(List.of("/showCurrentSettings"), true),
    SHOW_CURRENT_WEATHER(List.of("/showCurrentWeather", "🌥 Show current weather"), true),
    DEFAULT(List.of("/default"), false);

    private final List<String> messages;
    private final boolean availableForUserCall;

    Command(List<String> messages, boolean availableForUserCall) {
        this.messages = messages;
        this.availableForUserCall = availableForUserCall;
    }

    public static Command of(Message message) {
        String messageText = message.getText();
        Optional<Command> commandOptional;

        if (message.hasLocation()) {
            commandOptional = Optional.of(SAVE_LOCATION);
        } else if (isValidTime(message)) {
            commandOptional = Optional.of(SAVE_TIME);
        } else {
            commandOptional = matchMessage(messageText);
        }
        return commandOptional.orElse(UNKNOWN);
    }

    @SuppressWarnings("CallToSimpleGetterFromWithinClass")
    private static Optional<Command> matchMessage(String messageText) {
        return Arrays.stream(Command.values())
                .filter(command -> {
                    List<String> messages = command.getMessages();
                    return messages.contains(messageText) && command.isAvailableForUserCall();
                })
                .findFirst();
    }

    private static boolean isValidTime(Message message) {
        String text = message.getText();
        String regex = "^(0?\\d|1\\d|2[0-3]):[0-5]\\d$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }
}
