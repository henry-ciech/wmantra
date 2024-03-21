package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageSender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.LooseCoupling")
class SendMessageJobTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private ImageSender mockImageSender;

    @InjectMocks
    private SendMessageJob sendMessageJob;

    @SuppressWarnings("ChainedMethodCall")
    @SneakyThrows
    @Test
    void testExecuteMethod() {
        when(mockApplicationContext.getBean(ImageSender.class)).thenReturn(mockImageSender);
        JobExecutionContext mockContext = mock(JobExecutionContext.class);
        JobDataMap jobDataMap = new JobDataMap();
        long expectedChatId = 1L;
        jobDataMap.put("chatId", expectedChatId);

        Scheduler mockScheduler = mock(Scheduler.class);
        SchedulerContext mockSchedulerContext = mock(SchedulerContext.class);
        when(mockContext.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(mockContext.getScheduler()).thenReturn(mockScheduler);
        when(mockScheduler.getContext()).thenReturn(mockSchedulerContext);
        when(mockSchedulerContext.get("applicationContext")).thenReturn(mockApplicationContext);

        sendMessageJob.execute(mockContext);

        verify(mockImageSender).sendImageToTheUser(expectedChatId);
    }
}
