package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.ImageSender;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class ShowCurrentWeatherProcessor implements Processor {

    private final ImageSender imageSender;
    private final TelegramBot telegramBot;
    private final UserService userService;
    private final Command command;

    @Autowired
    ShowCurrentWeatherProcessor(ImageSender imageSender, TelegramBot telegramBot, UserService userService) {
        this.userService = userService;
        this.imageSender = imageSender;
        this.telegramBot = telegramBot;
        command = Command.SHOW_CURRENT_WEATHER;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();

        ConfigurationStage configurationStage = userService.resolveConfigStage(chatId);

        boolean noTimeStage = configurationStage == ConfigurationStage.NO_TIME;
        boolean completedStage = configurationStage == ConfigurationStage.COMPLETED;
        if (noTimeStage || completedStage) {
            imageSender.sendImageToTheUser(chatId);
        } else {
            MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
            telegramBot.onUpdateReceived(messageWithNewType);
        }
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
