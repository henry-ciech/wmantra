package eu.ciechanowiec.bot.processors;

import static org.junit.jupiter.api.Assertions.*;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ConfigProcessorTest {

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;
    @Autowired
    TelegramBot telegramBot;
    @Autowired
    ConfigProcessor configProcessor;

    @Test
    void commandShouldBeConfig() {
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        Processor processor = new ConfigProcessor(mockTelegramBot);

        Command command = processor.getCommandType();
        assertEquals(Command.CONFIG, command);
    }

    @SuppressWarnings("ChainedMethodCall")
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        MessageTemplater messageTemplater = new MessageTemplater();
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);

        String configMessage = messageTemplater.getAskConfigMessage();
        message.setText(configMessage);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.CONFIG);

        configProcessor.process(messageDTO);

        sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(3)).execute(sendMessageCaptor.capture());
        SendMessage capturedSendMessage = sendMessageCaptor.getAllValues().get(2);

        assertEquals(configMessage, capturedSendMessage.getText());
    }
}
