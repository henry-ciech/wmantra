package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.User;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SuppressWarnings("PMD.LooseCoupling")
class ReportsSchedulerTest {

    private static final String TEST = "test";
    private static final String GROUP = "group_";
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testScheduleJob() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        SchedulerContext context = scheduler.getContext();
        context.put("applicationContext", applicationContext);

        ReportsScheduler reportsScheduler = new ReportsScheduler(applicationContext);
        User user = new User(1L, 0.0, 0.0,
                LocalTime.of(0, 0), TEST, TEST, true);

        reportsScheduler.schedule(user);

        long chatId = user.getChatId();
        JobKey jobKey = new JobKey("Job_" + chatId, GROUP + chatId);
        TriggerKey triggerKey = new TriggerKey("Trigger_" + chatId, GROUP + chatId);

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        Trigger trigger = scheduler.getTrigger(triggerKey);

        assertAll(
                () -> assertTrue(scheduler.checkExists(jobKey)),
                () -> assertTrue(scheduler.checkExists(triggerKey)),
                () -> assertEquals(jobKey, jobDetail.getKey()),
                () -> assertEquals(triggerKey, trigger.getKey())
        );
    }

    @Test
    void testRescheduleJob() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        SchedulerContext context = scheduler.getContext();
        context.put("applicationContext", applicationContext);

        ReportsScheduler reportsScheduler = new ReportsScheduler(applicationContext);
        User user = new User(1L, 0.0, 0.0, LocalTime.of(0, 0),
                TEST, TEST, true);

        reportsScheduler.schedule(user);

        User userWithChangedTime = new User(user, LocalTime.of(1, 0));
        reportsScheduler.schedule(userWithChangedTime);

        JobKey jobKey = new JobKey("Job_" + userWithChangedTime.getChatId(),
                GROUP + userWithChangedTime.getChatId());
        TriggerKey triggerKey = new TriggerKey("Trigger_" + userWithChangedTime.getChatId(),
                GROUP + userWithChangedTime.getChatId());

        Trigger newTrigger = scheduler.getTrigger(triggerKey);
        CronTrigger cronTrigger = (CronTrigger) newTrigger;
        LocalTime time = userWithChangedTime.getTime();
        String expectedCronExpression = String.format("0 %d %d ? * *", time.getMinute(), time.getHour());

        assertAll(
                () -> assertTrue(scheduler.checkExists(jobKey)),
                () -> assertTrue(scheduler.checkExists(triggerKey)),
                () -> assertEquals(expectedCronExpression, cronTrigger.getCronExpression())
        );
    }
}
