package eu.ciechanowiec.bot.processors;

import eu.ciechanowiec.bot.model.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProcessorRegistry {

    private final List<Processor> processors;
    private final Processor defaultProcessor;

    @Autowired
    public ProcessorRegistry(List<Processor> processors, Processor defaultProcessor) {
        this.processors = new ArrayList<>(processors);
        this.defaultProcessor = defaultProcessor;
    }

    public Processor getProcessor(Command command) {
        Optional<Processor> first = processors.stream()
                .filter(processor -> {
                    Command commandType = processor.getCommandType();
                    return commandType == command;
                })
                .findFirst();
        return first
                .orElse(defaultProcessor);
    }
}
