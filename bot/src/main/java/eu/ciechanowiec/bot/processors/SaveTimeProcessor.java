package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
class SaveTimeProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final Command command;

    @Autowired
    SaveTimeProcessor(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        command = Command.SAVE_TIME;
    }

    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();
        boolean isLocationSpecified = userService.isLocationSpecified(chatId);

        if (isLocationSpecified) {
            log.info("Saving time");
            userService.saveTime(update);
            MessageDTO messageDTOToSend = new MessageDTO(update, Command.SHOW_CURRENT_SETTINGS);
            telegramBot.onUpdateReceived(messageDTOToSend);
        } else {
            MessageDTO messageDTOToSend = new MessageDTO(update, Command.ASK_LOCATION);
            telegramBot.onUpdateReceived(messageDTOToSend);
        }
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
