package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.KeyboardCreator;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
@Slf4j
class DefaultProcessor implements Processor {

    private final Command command;
    private final MessageTemplater messageTemplater;
    private final TelegramBot telegramBot;
    private final KeyboardCreator keyboardCreator;

    @Autowired
    DefaultProcessor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        command = Command.DEFAULT;
        this.messageTemplater = new MessageTemplater();
        keyboardCreator = new KeyboardCreator();
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();

        Long chatId = message.getChatId();
        String errorMessage = messageTemplater.getErrorMessage();
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, errorMessage);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardCreator.createReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        log.error("Sending error message");
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}

