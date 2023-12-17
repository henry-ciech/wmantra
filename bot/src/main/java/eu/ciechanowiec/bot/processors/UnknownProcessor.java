package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
class UnknownProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;

    @Autowired
    UnknownProcessor(TelegramBot telegramBot, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.messageTemplater = messageTemplater;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Update update = messageDTO.update();
        Message message = update.getMessage();
        long chatId = message.getChatId();

        sendIncorrectMessageNotify(chatId, messageDTO);
    }

    @Override
    public Command getCommandType() {
        return Command.UNKNOWN;
    }

    @SneakyThrows
    private void sendIncorrectMessageNotify(long chatId, MessageDTO messageDTO) {
        String messageToPrint = messageTemplater.getIncorrectMessageNotification();
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, messageToPrint);
        telegramBot.execute(sendMessage);
        MessageDTO changedMessage = messageDTO.withNewMessageType(Command.SHOW_CURRENT_SETTINGS);
        telegramBot.onUpdateReceived(changedMessage);
    }
}
