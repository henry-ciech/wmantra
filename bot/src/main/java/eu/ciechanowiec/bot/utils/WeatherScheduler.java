package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.service.ImageService;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.TimeZone;

@Component
public class WeatherScheduler {

    private final ImageService imageService;

    @Autowired
    WeatherScheduler(ImageService imageService) {
        this.imageService = imageService;
    }

    @SneakyThrows
    public void schedule(User user) {
        LocalTime time = user.getTime();
        long chatId = user.getChatId();
        int hour = time.getHour();
        int minute = time.getMinute();

        String jobName = "Job_" + chatId;
        String triggerName = "Trigger_" + chatId;
        String groupName = "group_" + chatId;

        JobKey jobKey = new JobKey(jobName, groupName);
        TriggerKey triggerKey = new TriggerKey(triggerName, groupName);

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        if (scheduler.checkExists(jobKey)) {
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
                            .inTimeZone(TimeZone.getTimeZone("UTC")))
                    .build();

             scheduler.setJobFactory(new JobFactoryWithParameters(chatId, imageService));

            scheduler.rescheduleJob(triggerKey, newTrigger);
        } else {
            JobDetail job = JobBuilder.newJob(SendMessageJob.class)
                    .withIdentity(jobKey)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
                            .inTimeZone(TimeZone.getTimeZone("UTC")))
                    .build();

            scheduler.setJobFactory(new JobFactoryWithParameters(chatId, imageService));

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        }
    }
}
