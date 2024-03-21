package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import eu.ciechanowiec.bot.model.MessageDTO;
import eu.ciechanowiec.bot.service.TelegramBot;
import eu.ciechanowiec.bot.utils.MessageTemplater;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@SpringBootTest
@SuppressWarnings("PMD.TooManyStaticImports")
@ExtendWith(MockitoExtension.class)
class AskLocationProcessorTest {

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;
    @Autowired
    private TelegramBot spyBot;

    @Autowired
    private AskLocationProcessor askLocationProcessor;

    @AfterEach
    void resetSpy() {
        Mockito.reset(spyBot);
    }

    @Test
    void commandShouldBeAskLocation() {
        Command command = askLocationProcessor.getCommandType();
        assertEquals(Command.ASK_LOCATION, command);
    }

    @SuppressWarnings({"ChainedMethodCall", "ReturnOfNull"})
    @SneakyThrows
    @Test
    void processShouldSendMessage() {
        Mockito.doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));

        MessageTemplater messageTemplater = new MessageTemplater();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);

        String askLocationMessage = messageTemplater.getAskLocationMessage();
        message.setText(askLocationMessage);
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        MessageDTO messageDTO = new MessageDTO(update, Command.ASK_LOCATION);

        askLocationProcessor.process(messageDTO);

        SendMessage value = sendMessageCaptor.getValue();
        String text = value.getText();
        SendMessage capture = sendMessageCaptor.capture();
        assertAll(
                () -> verify(spyBot, times(1)).execute(capture),
                () -> assertEquals(askLocationMessage, text)
        );
    }
}
