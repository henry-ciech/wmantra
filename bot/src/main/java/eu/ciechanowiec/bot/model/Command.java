package eu.ciechanowiec.bot.model;

import eu.ciechanowiec.bot.utils.MessageTemplater;
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
    CONFIG(List.of("/config", MessageTemplater.CONFIGURE_BUTTON_TEXT), true),
    SHOW_CURRENT_SETTINGS(List.of("/showCurrentSettings"), true),
    SHOW_CURRENT_WEATHER(List.of("/showCurrentWeather", MessageTemplater.SHOW_CURRENT_SETTINGS_BUTTON_TEXT),
            true),
    ASK_LOCATION(List.of("/askLocation"), false),
    ASK_TIME(List.of("/askTime"), false),
    UNKNOWN(List.of("/unknown"), false),
    SAVE_LOCATION(List.of("/saveLocation"), false),
    SAVE_TIME(List.of("/saveTime"), false),
    DEFAULT(List.of("/default"), false);

    private final List<String> messageTexts;
    private final boolean availableForUserCall;

    Command(List<String> messageTexts, boolean availableForUserCall) {
        this.messageTexts = messageTexts;
        this.availableForUserCall = availableForUserCall;
    }

    public static Command of(Message message) {
        String messageText = message.getText();
        Optional<Command> commandOptional;

        boolean hasLocation = message.hasLocation();
        if (hasLocation) {
            commandOptional = Optional.of(SAVE_LOCATION);
        } else if (isValidTime(messageText)) {
            commandOptional = Optional.of(SAVE_TIME);
        } else {
            commandOptional = matchMessage(messageText);
        }
        return commandOptional.orElse(UNKNOWN);
    }

    @SuppressWarnings("CallToSimpleGetterFromWithinClass")
    private static Optional<Command> matchMessage(String messageText) {
        Command[] commandValues = Command.values();
        return Arrays.stream(commandValues)
                .filter(command -> {
                    List<String> messageTexts = command.getMessageTexts();
                    boolean contains = messageTexts.contains(messageText);
                    boolean availableForUserCall = command.isAvailableForUserCall();
                    return contains && availableForUserCall;
                })
                .findFirst();
    }

    private static boolean isValidTime(CharSequence text) {
        String regex = "^(0?\\d|1\\d|2[0-3]):[0-5]\\d$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }
}
