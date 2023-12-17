package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import eu.ciechanowiec.bot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
class AskTimeProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;

    @Autowired
    AskTimeProcessor(TelegramBot telegramBot, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.messageTemplater = messageTemplater;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();

        Long chatId = message.getChatId();
        String chatIdStr = String.valueOf(chatId);
        String messageToPrint = messageTemplater.getAskTimeMessage();

        SendMessage sendMessage = new SendMessage(chatIdStr, messageToPrint);
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return Command.ASK_TIME;
    }


}
