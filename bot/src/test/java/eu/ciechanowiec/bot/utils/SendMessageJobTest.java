package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageSender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendMessageJobTest {

    @SneakyThrows
    @Test
    public void testExecuteMethod() {
        // Mock the necessary objects
        JobExecutionContext mockContext = mock(JobExecutionContext.class);
        JobDataMap jobDataMap = new JobDataMap();
        long expectedChatId = 1L;
        jobDataMap.put("chatId", expectedChatId);

        Scheduler mockScheduler = mock(Scheduler.class);
        SchedulerContext mockSchedulerContext = mock(SchedulerContext.class);
        ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
        ImageSender mockImageSender = mock(ImageSender.class);

        // Setup the mocks to return the appropriate objects
        when(mockContext.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(mockContext.getScheduler()).thenReturn(mockScheduler);
        when(mockScheduler.getContext()).thenReturn(mockSchedulerContext);
        when(mockSchedulerContext.get("applicationContext")).thenReturn(mockApplicationContext);
        when(mockApplicationContext.getBean(ImageSender.class)).thenReturn(mockImageSender);

        // Execute the method
        SendMessageJob sendImageJob = new SendMessageJob();
        sendImageJob.execute(mockContext);

        verify(mockImageSender).sendImageToTheUser(expectedChatId);
    }
}
