package com.davv1d.service;

import com.davv1d.event.PasswordReminderTokenGenerated;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class PasswordReminderTokenScheduler {
    public static final String USER_ID = "userId";
    private static final String TRIGGER_GROUP = "token-triggers";
    private static final String JOB_DETAIL_GROUP = "token-jobs";
    private static final long time = 60;
    private final Scheduler scheduler;

    public void startSchedule(PasswordReminderTokenGenerated event) {
        JobDetail jobDetail = buildJobDetail(event);
        ZonedDateTime now = ZonedDateTime.now().plusSeconds(time);
        Trigger trigger = buildTrigger(jobDetail, now);
        try {
            Date date = scheduler.scheduleJob(jobDetail, trigger);
            System.out.println(date);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private JobDetail buildJobDetail(PasswordReminderTokenGenerated event) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(USER_ID, event.getUserId());
        return JobBuilder.newJob(ReminderTokenJob.class)
                .withIdentity(event.getUserId(), JOB_DETAIL_GROUP)
                .withDescription("Password reminder token job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime now) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP)
                .withDescription("Password reminder token trigger")
                .startAt(Date.from(now.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public void stopSchedule(String userId) {
        TriggerKey triggerKey = new TriggerKey(userId, TRIGGER_GROUP);
        try {
            scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
