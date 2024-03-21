package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class ProcessorRegistryTest {

    @Autowired
    private ProcessorRegistry processorRegistry;

    @ParameterizedTest
    @MethodSource("args")
    void commandShouldReturnCorrespondingProcessor(Command expectedCommand) {
        Processor processor = processorRegistry.getProcessor(expectedCommand);
        Command actualCommand = processor.getCommandType();
        assertEquals(expectedCommand, actualCommand);
    }

    static Stream<Arguments> args() {
        return Stream.of(
                arguments(Command.START),
                arguments(Command.ASK_LOCATION),
                arguments(Command.ASK_TIME),
                arguments(Command.CONFIG),
                arguments(Command.UNKNOWN),
                arguments(Command.SAVE_LOCATION),
                arguments(Command.SAVE_TIME),
                arguments(Command.SHOW_CURRENT_SETTINGS),
                arguments(Command.SHOW_CURRENT_WEATHER),
                arguments(Command.DEFAULT)
        );
    }
}
