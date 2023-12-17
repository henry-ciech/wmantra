package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.service.ImageService;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

class JobFactoryWithParameters implements JobFactory {

    private final long chatId;
    private final ImageService imageService;

    JobFactoryWithParameters(long chatId, ImageService imageService) {
        this.chatId = chatId;
        this.imageService = imageService;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        JobDetail jobDetail = bundle.getJobDetail();
        Class<? extends Job> jobClass = jobDetail.getJobClass();
        if (jobClass.equals(SendMessageJob.class)) {
            return new SendMessageJob(chatId, imageService);
        }
        throw new SchedulerException("Unsupported job class: " + jobClass);
    }
}
