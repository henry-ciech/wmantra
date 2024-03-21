package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageSender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.utils.StringKeyDirtyFlagMap;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Slf4j
public class SendMessageJob implements Job {

    @SneakyThrows
    @Override
    @SuppressWarnings("PMD.LooseCoupling")
    public void execute(JobExecutionContext context) {
        log.info("Send scheduled message");
        StringKeyDirtyFlagMap mergedJobDataMap = context.getMergedJobDataMap();
        long chatId = mergedJobDataMap.getLong("chatId");
        Scheduler scheduler = context.getScheduler();
        StringKeyDirtyFlagMap schedulerContext = scheduler.getContext();
        ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");
        ImageSender imageSender = applicationContext.getBean(ImageSender.class);

        String chatIdStr = String.valueOf(chatId);

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatIdStr);

        imageSender.sendImageToTheUser(chatId);
    }
}
