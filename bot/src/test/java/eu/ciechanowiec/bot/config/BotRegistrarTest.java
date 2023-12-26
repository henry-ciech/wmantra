package eu.ciechanowiec.bot.config;

import eu.ciechanowiec.bot.service.TelegramBot;
import lombok.SneakyThrows;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BotRegistrarTest {

    @SneakyThrows
    @Test
    void initShouldRegisterBot() {
        TelegramBot mockBot = mock(TelegramBot.class);

        try (MockedConstruction<TelegramBotsApi> mocked =
                     Mockito.mockConstruction(TelegramBotsApi.class, (mock, context) -> Mockito.when(
                             mock.registerBot(any(LongPollingBot.class))).thenReturn(mock(BotSession.class)))) {

            BotRegistrar botRegistrar = new BotRegistrar(mockBot);
            botRegistrar.init();

            List<?> constructed = ((MockedConstruction<?>) mocked).constructed();
            TelegramBotsApi telegramBotsApi = (TelegramBotsApi) constructed.get(0);
            TelegramBotsApi verify = verify(telegramBotsApi);
            verify.registerBot(mockBot);
        }
    }
}
