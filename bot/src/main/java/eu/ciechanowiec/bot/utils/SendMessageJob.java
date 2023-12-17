package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class SendMessageJob implements Job {

    private final long chatId;
    private final ImageService imageService;

    SendMessageJob(long chatId, ImageService imageService) {
        this.chatId = chatId;
        this.imageService = imageService;
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {
        String chatIdStr = String.valueOf(chatId);

        SendPhoto photo = new SendPhoto();

        photo.setChatId(chatIdStr);

        imageService.sendImageToTheUser(chatId);
    }
}
