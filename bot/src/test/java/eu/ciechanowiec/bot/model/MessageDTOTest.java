package eu.ciechanowiec.bot.model;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

class MessageDTOTest {

    @Test
    void withNewMessageTypeShouldChangeCommand() {
        Update update = new Update();
        Command command = Command.UNKNOWN;
        MessageDTO messageDTO = new MessageDTO(update, command);
        MessageDTO messageDTOWithNewType = messageDTO.withNewMessageType(Command.DEFAULT);
        MessageDTO expectedMessageDTO = new MessageDTO(update, Command.DEFAULT);

        assertEquals(expectedMessageDTO, messageDTOWithNewType);
    }
}
