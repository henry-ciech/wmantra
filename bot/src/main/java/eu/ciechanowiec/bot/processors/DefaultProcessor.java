package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
class DefaultProcessor implements Processor {

    private final ApplicationContext applicationContext;
    private final Command command;

    @Autowired
    DefaultProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        command = Command.DEFAULT;
    }

    @SneakyThrows
    @Override
    public void process(MessageDTO messageDTO) {
        TelegramBot telegramBot = applicationContext.getBean(TelegramBot.class);
        Update update = messageDTO.update();
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        SendMessage sendMessage
                = new SendMessage(String.valueOf(chatId), "Something went wrong, try later");
        telegramBot.execute(sendMessage);
    }

    @Override
    public Command getCommandType() {
        return command;
    }
}
