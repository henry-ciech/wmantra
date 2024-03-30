package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.utils.KeyboardCreator;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
class AskTimeProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;
    private final Command command;
    private final KeyboardCreator keyboardCreator;

    @Autowired
    AskTimeProcessor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.messageTemplater = new MessageTemplater();
        command = Command.ASK_TIME;
        keyboardCreator = new KeyboardCreator();
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        log.info("Asking time from the user");
        Message message = messageDTO.getMessage();

        Long chatId = message.getChatId();
        String messageToPrint = messageTemplater.getAskTimeMessage();

        SendMessage sendMessage = createSendMessage(chatId, messageToPrint);
        telegramBot.execute(sendMessage);
    }

    private SendMessage createSendMessage(Long chatId, String messageText) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageText);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardCreator.createReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
