package eu.ciechanowiec.bot.processors;

import static org.junit.jupiter.api.Assertions.*;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class DefaultProcessorTest {

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Autowired
    TelegramBot spyBot;
    @Autowired
    DefaultProcessor defaultProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void commandShouldBeDefault() {
        TelegramBot mockTelegramBot = Mockito.mock(TelegramBot.class);
        Processor processor = new DefaultProcessor(mockTelegramBot);

        Command command = processor.getCommandType();
        assertEquals(Command.DEFAULT, command);
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

        String defaultMessage = messageTemplater.getErrorMessage();
        message.setText(defaultMessage);
        message.setChat(chat);
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.DEFAULT);

        defaultProcessor.process(messageDTO);

        verify(spyBot, times(6)).execute(sendMessageCaptor.capture());
        List<SendMessage> allValues = sendMessageCaptor.getAllValues();
        System.out.println(allValues);
        SendMessage capturedSendMessage = allValues.get(5);

        assertEquals(defaultMessage, capturedSendMessage.getText());
    }
}
