package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageSender;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class SendMessageJob implements Job {

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        long chatId = mergedJobDataMap.getLong("chatId");
        Scheduler scheduler = context.getScheduler();
        SchedulerContext schedulerContext = scheduler
                .getContext();
        ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");
        ImageSender imageSender = applicationContext.getBean(ImageSender.class);

        String chatIdStr = String.valueOf(chatId);

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatIdStr);

        imageSender.sendImageToTheUser(chatId);
    }
}
