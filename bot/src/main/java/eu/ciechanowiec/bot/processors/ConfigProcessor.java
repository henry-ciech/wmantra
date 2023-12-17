package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
class ConfigProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;

    @Autowired
    ConfigProcessor(TelegramBot telegramBot, MessageTemplater messageTemplater) {
        this.telegramBot = telegramBot;
        this.messageTemplater = messageTemplater;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();

        Long chatId = message.getChatId();
        String chatIdStr = String.valueOf(chatId);
        String messageToPrint = messageTemplater.getAskConfigMessage();

        SendMessage sendMessage = new SendMessage(chatIdStr, messageToPrint);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return Command.CONFIG;
    }
}
