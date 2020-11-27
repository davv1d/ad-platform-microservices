package com.davv1d.handler;

import com.davv1d.event.*;
import com.davv1d.service.DistributedEventBus;
import com.davv1d.service.PasswordReminderTokenScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventHandler {
    private final DistributedEventBus distributedEventBus;
    private final PasswordReminderTokenScheduler passwordReminderTokenScheduler;

    @EventListener
    public void handle(UserCreated event) {
        System.out.println("Catch user created event | userId = " + event.getUserId());
        distributedEventBus.sendUserCreatedEvent(event);
    }

    @EventListener
    public void handle(UserActivated event) {
        System.out.println("Catch user activated event | userId =  " + event.getUserId());
        distributedEventBus.sendUserActivatedEvent(event);
    }

    @EventListener
    public void handle(UserDeactivated event) {
        System.out.println("Catch user deactivated event | userId = " + event.getUserId());
        distributedEventBus.sendUserDeactivatedEvent(event);
    }

    @EventListener
    public void handle(PasswordReminderTokenGenerated event) {
        System.out.println("Catch password reminder token generated event | userId = " + event.getUserId());
        distributedEventBus.sendPasswordReminderTokenGeneratedEvent(event);
        passwordReminderTokenScheduler.startSchedule(event);
    }

    @EventListener
    public void handle(UserPasswordChanged event) {
        System.out.println("Catch user password changed event | userId = " + event.getUserId());
        passwordReminderTokenScheduler.deleteJob(event.getUserId());
    }
}
