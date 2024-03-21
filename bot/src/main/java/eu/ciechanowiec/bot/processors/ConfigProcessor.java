package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Slf4j
class ConfigProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;
    private final Command command;

    @Autowired
    ConfigProcessor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.messageTemplater = new MessageTemplater();
        command = Command.CONFIG;
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
        log.info("Sending configure message");
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
