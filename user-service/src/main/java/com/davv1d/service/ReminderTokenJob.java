package com.davv1d.service;

import com.davv1d.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import static com.davv1d.service.PasswordReminderTokenScheduler.*;

@Component
@RequiredArgsConstructor
public class ReminderTokenJob implements Job {
    private final UserRepository userRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Executing job");
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String userId = jobDataMap.getString(USER_ID);
        userRepository.findById(userId).ifPresent(user -> {
            user.removePasswordReminderToken();
            String email = userRepository.save(user).getEmail();
            System.out.println("Execute job " + email);
        });
    }
}
