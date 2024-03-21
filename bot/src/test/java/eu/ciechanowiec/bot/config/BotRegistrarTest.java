package eu.ciechanowiec.bot.config;

import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BotRegistrarTest {

    @Autowired
    private TelegramBot spyBot;
    @Autowired
    private BotRegistrar botRegistrar;

    @AfterEach
    void resetSpy() {
        reset(spyBot);
    }

    @SneakyThrows
    @Test
    void initShouldRegisterBot() {
        try (MockedConstruction<TelegramBotsApi> mocked =
                     mockConstruction(TelegramBotsApi.class, (mock, context) -> when(
                             mock.registerBot(any(LongPollingBot.class))).thenReturn(mock(BotSession.class)))) {
            botRegistrar.init();

            List<?> constructed = mocked.constructed();
            TelegramBotsApi mockedTelegramBotsApi = (TelegramBotsApi) constructed.get(0);
            TelegramBotsApi verifiedTelegramBotsApi = verify(mockedTelegramBotsApi);
            verifiedTelegramBotsApi.registerBot(spyBot);
        }
    }
}
