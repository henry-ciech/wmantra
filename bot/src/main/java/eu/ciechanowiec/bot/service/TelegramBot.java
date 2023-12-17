package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.processors.Processor;
import eu.ciechanowiec.bot.processors.ProcessorRegistrar;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ApplicationContext applicationContext;

    @Autowired
    TelegramBot(BotConfig botConfig, DefaultBotOptions options, ApplicationContext applicationContext) {
        super(options, botConfig.getToken());
        this.botConfig = botConfig;
        this.applicationContext = applicationContext;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        MessageDTO messageDTO = normalize(update);
        onUpdateReceived(messageDTO);
    }

    public void onUpdateReceived(MessageDTO messageDTO) {
        ProcessorRegistrar processorRegistrar = applicationContext.getBean(ProcessorRegistrar.class);
        Command command = messageDTO.command();

        Processor processor = processorRegistrar.getProcessor(command);
        processor.process(messageDTO);
    }

    private MessageDTO normalize(Update update) {
        Message message = update.getMessage();
        Command command = Command.of(message);

        return switch (command) {
            case START, CONFIG, SHOW_CURRENT_SETTINGS, SHOW_CURRENT_WEATHER, SAVE_LOCATION, SAVE_TIME ->
                    new MessageDTO(update, command);
            default -> new MessageDTO(update, Command.UNKNOWN);
        };
    }
}
