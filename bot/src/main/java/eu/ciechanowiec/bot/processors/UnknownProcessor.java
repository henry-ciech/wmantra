package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.KeyboardCreator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
class UnknownProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;
    private final Command command;
    private final KeyboardCreator keyboardCreator;

    @Autowired
    UnknownProcessor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.messageTemplater = new MessageTemplater();
        command = Command.UNKNOWN;
        keyboardCreator = new KeyboardCreator();
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();
        sendErrorMessage(chatId, messageDTO);
    }

    @SneakyThrows
    private void sendErrorMessage(long chatId, MessageDTO messageDTO) {
        String messageToPrint = messageTemplater.getWrongDataMessage();
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageToPrint);
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardCreator.createReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        telegramBot.execute(sendMessage);
        MessageDTO messageWithNewType = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
        telegramBot.onUpdateReceived(messageWithNewType);
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
