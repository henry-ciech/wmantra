package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.ConfigurationStage;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.ImageService;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ShowCurrentWeatherProcessor implements Processor {

    private final ImageService imageService;
    private final TelegramBot telegramBot;
    private final UserService userService;

    @Autowired
    ShowCurrentWeatherProcessor(ImageService imageService, TelegramBot telegramBot, UserService userService) {
        this.userService = userService;
        this.imageService = imageService;
        this.telegramBot = telegramBot;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();

        ConfigurationStage configurationStage = userService.getConfigurationStage(chatId);

        if (configurationStage == ConfigurationStage.COMPLETED) {
            imageService.sendImageToTheUser(chatId);
        } else {
            MessageDTO changedMessage = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
            telegramBot.onUpdateReceived(changedMessage);
        }
    }

    @Override
    public Command getCommandType() {
        return Command.SHOW_CURRENT_WEATHER;
    }
}
