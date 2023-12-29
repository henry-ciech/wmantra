package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.*;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import eu.ciechanowiec.bot.service.LocationRetriever;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import eu.ciechanowiec.bot.utils.ReportsScheduler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
class ShowCurrentSettingsProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final LocationRetriever locationRetriever;
    private final ReportsScheduler reportsScheduler;
    private final MessageTemplater messageTemplater;
    private final Command command;

    @Autowired
    ShowCurrentSettingsProcessor(TelegramBot telegramBot, UserService userService, LocationRetriever locationRetriever,
                                 ReportsScheduler reportsScheduler, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.locationRetriever = locationRetriever;
        this.reportsScheduler = reportsScheduler;
        this.messageTemplater = messageTemplater;
        command = Command.SHOW_CURRENT_SETTINGS;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();
        Long chatId = message.getChatId();
        ConfigurationStage configurationStage = userService.determinConfigurationStage(chatId);

        switch (configurationStage) {
            case COMPLETED -> processCompletedConfiguration(chatId);
            case NO_TIME -> processConfiguration(messageDTO, Command.ASK_TIME);
            case NO_LOCATION_AND_TIME -> processConfiguration(messageDTO, Command.ASK_LOCATION);
        }
    }

    @Override
    public Command getCommandType() {
        return command;
    }

    @SneakyThrows
    private void processCompletedConfiguration(Long chatId) {
        User user = userService.findUser(chatId);
        User userWithAdjustedTime = getUserWithAdjustedTime(chatId);
        String messageToSend = prepareMessageTextToSend(userWithAdjustedTime);
        SendMessage sendMessage = createSendMessage(chatId, messageToSend);
        reportsScheduler.schedule(user);
        telegramBot.execute(sendMessage);
    }

    private User adjustUserTime(User user, ZoneOffset zoneOffset) {
        LocalTime utcTime = user.getTime();
        OffsetTime timeInUTCOffset = utcTime.atOffset(ZoneOffset.UTC);
        OffsetTime adjustedTime = timeInUTCOffset.withOffsetSameInstant(zoneOffset);
        return new User(user, adjustedTime.toLocalTime());
    }

    private User getUserWithAdjustedTime(Long chatId) {
        double latitude = userService.findLatitude(chatId);
        double longitude = userService.findLongitude(chatId);
        User user = userService.findUser(chatId);
        ZoneOffset zoneOffset = userService.getTimeZone(longitude, latitude);
        return adjustUserTime(user, zoneOffset);
    }

    private void processConfiguration(MessageDTO messageDTO, Command command) {
        MessageDTO messageWithNewType = messageDTO.withNewMessageType(command);
        telegramBot.onUpdateReceived(messageWithNewType);
    }

    private String prepareMessageTextToSend(User user) {
        Double latitude = user.getLatitude();
        Double longitude = user.getLongitude();
        Location location = locationRetriever.retrieveLocationData(latitude, longitude);
        return messageTemplater.getWhenNextReports(location, user);
    }

    private SendMessage createSendMessage(Long chatId, String messageText) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(createReplyKeyboardMarkup());

        return sendMessage;
    }

    private ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add(MessageTemplater.SHOW_CURRENT_SETTINGS_BUTTON_TEXT);
        secondRow.add(MessageTemplater.CONFIGURE_BUTTON_TEXT);
        keyboardRows.add(firstRow);
        keyboardRows.add(secondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
