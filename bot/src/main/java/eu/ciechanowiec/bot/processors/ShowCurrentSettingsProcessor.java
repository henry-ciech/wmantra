package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.*;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import eu.ciechanowiec.bot.service.LocationRetriever;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import eu.ciechanowiec.bot.utils.ReportsScheduler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Service
@SuppressWarnings("ClassFanOutComplexity")
@Slf4j
class ShowCurrentSettingsProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final LocationRetriever locationRetriever;
    private final ReportsScheduler reportsScheduler;
    private final MessageTemplater messageTemplater;
    private final Command command;

    @Autowired
    @SuppressWarnings({"PMD.ExcessiveParameterList", "ParameterNumber"})
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
        ConfigurationStage configurationStage = userService.resolveConfigStage(chatId);

        switch (configurationStage) {
            case COMPLETED -> processCompletedConfiguration(chatId);
            case NO_TIME -> processConfiguration(messageDTO, Command.ASK_TIME);
            case NO_LOCATION_AND_TIME -> processConfiguration(messageDTO, Command.ASK_LOCATION);
            default -> processConfiguration(messageDTO, Command.DEFAULT);
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
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageToSend);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        log.info("Message scheduled");
        reportsScheduler.schedule(user);
        log.info("Showing current settings");
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
}
