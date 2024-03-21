package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.User;
import eu.ciechanowiec.bot.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.StringKeyDirtyFlagMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.TimeZone;

@Service
@Slf4j
public class ReportsScheduler {

    private final ApplicationContext applicationContext;

    @Autowired
    public ReportsScheduler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SneakyThrows
    public void schedule(User user) {
        log.info("Schedule report");
        Scheduler scheduler = getScheduler();
        JobKey jobKey = createJobKey(user);
        TriggerKey triggerKey = createTriggerKey(user);

        JobDetail job = createJobDetail(jobKey, user);
        Trigger trigger = createTrigger(triggerKey, user);

        if (scheduler.checkExists(jobKey)) {
            scheduler.rescheduleJob(triggerKey, trigger);
        } else {
            scheduleJob(scheduler, job, trigger);
        }
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private Scheduler getScheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        StringKeyDirtyFlagMap context = scheduler.getContext();
        context.put("applicationContext", applicationContext);
        return scheduler;
    }

    private JobKey createJobKey(User user) {
        long chatId = user.getChatId();
        return new JobKey("Job_" + chatId, "group_" + chatId);
    }

    private TriggerKey createTriggerKey(User user) {
        long chatId = user.getChatId();
        return new TriggerKey("Trigger_" + chatId, "group_" + chatId);
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private JobDetail createJobDetail(JobKey jobKey, User user) {
        long chatId = user.getChatId();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("chatId", chatId);

        return JobBuilder.newJob(SendMessageJob.class)
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger createTrigger(TriggerKey triggerKey, User user) {
        LocalTime time = user.getTime();
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(time.getHour(), time.getMinute())
                        .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }

    private void scheduleJob(Scheduler scheduler, JobDetail job, Trigger trigger) throws SchedulerException {
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
        scheduler.scheduleJob(job, trigger);
    }
}
