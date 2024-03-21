package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Slf4j
public class AskLocationProcessor implements Processor {

    private final TelegramBot telegramBot;
    private final MessageTemplater messageTemplater;
    private final Command command;

    @Autowired
    AskLocationProcessor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.messageTemplater = new MessageTemplater();
        command = Command.ASK_LOCATION;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        log.info("asking location");
        Message message = messageDTO.getMessage();

        Long chatId = message.getChatId();
        String chatIdStr = String.valueOf(chatId);
        String messageToPrint = messageTemplater.getAskLocationMessage();

        SendMessage sendMessage = new SendMessage(chatIdStr, messageToPrint);
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
