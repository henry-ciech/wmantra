package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.processors.Processor;
import eu.ciechanowiec.bot.processors.ProcessorRegistry;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Primary
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ApplicationContext applicationContext;

    @Autowired
    TelegramBot(BotConfig botConfig, DefaultBotOptions options, ApplicationContext applicationContext) {
        super(options, botConfig.getToken());
        this.botConfig = botConfig;
        this.applicationContext = applicationContext;
        System.out.println();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        MessageDTO messageDTO = transformUpdate(update);
        onUpdateReceived(messageDTO);
    }

    @SneakyThrows
    public void onUpdateReceived(MessageDTO messageDTO) {
        ProcessorRegistry processorRegistry = applicationContext.getBean(ProcessorRegistry.class);
        Command command = messageDTO.command();

        Processor processor = processorRegistry.getProcessor(command);
        processor.getCommandType();
        processor.process(messageDTO);
    }

    private MessageDTO transformUpdate(Update update) {
        Message message = update.getMessage();
        Command command = Command.of(message);

        return switch (command) {
            case START, CONFIG, SHOW_CURRENT_SETTINGS, SHOW_CURRENT_WEATHER, SAVE_LOCATION, SAVE_TIME ->
                    new MessageDTO(update, command);
            default -> new MessageDTO(update, Command.UNKNOWN);
        };
    }
}
