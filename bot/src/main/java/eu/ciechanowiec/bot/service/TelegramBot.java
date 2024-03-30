package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.config.BotConfig;
import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.processors.Processor;
import eu.ciechanowiec.bot.processors.ProcessorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final Executor telegramBotExecutor;
    private final ApplicationContext applicationContext;

    @Autowired
    TelegramBot(BotConfig botConfig, DefaultBotOptions options, ApplicationContext applicationContext) {
        super(options, botConfig.getToken());
        this.botConfig = botConfig;
        this.applicationContext = applicationContext;
        telegramBotExecutor = applicationContext.getBean(Executor.class);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            telegramBotExecutor.execute(() -> onUpdateReceived(update));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        MessageDTO messageDTO = transformUpdate(update);
        log.info("Message command: {}", messageDTO.command());
        onUpdateReceived(messageDTO);
    }

    public void onUpdateReceived(MessageDTO messageDTO) {
        ProcessorRegistry processorRegistry = applicationContext.getBean(ProcessorRegistry.class);
        Command command = messageDTO.command();

        Processor processor = processorRegistry.getProcessor(command);
        processor.process(messageDTO);
    }

    private MessageDTO transformUpdate(Update update) {
        UserService userService = applicationContext.getBean(UserService.class);
        Message message = update.getMessage();
        Command command = Command.getCommandFromMessage(message);

        Long chatId = message.getChatId();
        boolean userExists = userService.isUserExists(chatId);
        if (!userExists) {
            return new MessageDTO(update, Command.START);
        }

        return switch (command) {
            case START, CONFIG, SHOW_CURRENT_SETTINGS, SHOW_CURRENT_WEATHER, SAVE_LOCATION, SAVE_TIME ->
                    new MessageDTO(update, command);
            default -> new MessageDTO(update, Command.UNKNOWN);
        };
    }
}
