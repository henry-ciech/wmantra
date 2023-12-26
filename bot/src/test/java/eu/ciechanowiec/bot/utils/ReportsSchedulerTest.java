package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReportsSchedulerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testScheduleJob() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        SchedulerContext context = scheduler.getContext();
        context.put("applicationContext", applicationContext);

        ReportsScheduler reportsScheduler = new ReportsScheduler(applicationContext);
        User user = new User(1L, 0.0, 0.0,
                LocalTime.of(0, 0), "test", "test", true);


        // Act
        reportsScheduler.schedule(user);
        JobKey jobKey = new JobKey("Job_" + user.getChatId(), "group_" + user.getChatId());
        TriggerKey triggerKey = new TriggerKey("Trigger_" + user.getChatId(), "group_" + user.getChatId());

        // Assert
        assertTrue(scheduler.checkExists(jobKey));
        assertTrue(scheduler.checkExists(triggerKey));

        // Optionally, check the details of the job and trigger
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        Trigger trigger = scheduler.getTrigger(triggerKey);

        assertEquals(jobKey, jobDetail.getKey());
        assertEquals(triggerKey, trigger.getKey());
        // Add more assertions as necessary
    }

    @Test
    public void testRescheduleJob() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        SchedulerContext context = scheduler.getContext();
        context.put("applicationContext", applicationContext);

        ReportsScheduler reportsScheduler = new ReportsScheduler(applicationContext);
        User user = new User(1L, 0.0, 0.0, LocalTime.of(0, 0), "test", "test", true);

        // First schedule
        reportsScheduler.schedule(user);

        // Modify user's time to trigger rescheduling
        User userWithChangedTime = new User(user, LocalTime.of(1, 0));

        // Reschedule
        reportsScheduler.schedule(userWithChangedTime);

        JobKey jobKey = new JobKey("Job_" + userWithChangedTime.getChatId(), "group_" + userWithChangedTime.getChatId());
        TriggerKey triggerKey = new TriggerKey("Trigger_" + userWithChangedTime.getChatId(), "group_" + userWithChangedTime.getChatId());

        // Assert
        assertTrue(scheduler.checkExists(jobKey));
        assertTrue(scheduler.checkExists(triggerKey));

        // Check if the trigger is rescheduled
        Trigger newTrigger = scheduler.getTrigger(triggerKey);
        CronTrigger cronTrigger = (CronTrigger) newTrigger;
        String expectedCronExpression = String.format("0 %d %d ? * *", userWithChangedTime.getTime().getMinute(), userWithChangedTime.getTime().getHour());

        assertEquals(expectedCronExpression, cronTrigger.getCronExpression());
    }
}
