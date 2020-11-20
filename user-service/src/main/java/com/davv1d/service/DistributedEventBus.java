package com.davv1d.service;

import com.davv1d.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedEventBus {
    private static final String USER_BINDING = "user-out-0";
    private static final String USER_CREATE_BINDING = "user_create-out-0";
    private static final String USER_ACTIVATE_BINDING = "user_activate-out-0";
    private static final String USER_DEACTIVATE_BINDING = "user_deactivate-out-0";
    private static final String USER_TOKEN_BINDING = "user_token-out-0";

    private final StreamBridge streamBridge;

    public void sendUserCreatedEvent(UserCreated event) {
        Message<UserCreated> message = buildMessage(event, "userCreated");
        streamBridge.send(USER_BINDING, message);
    }

    public void sendUserActivatedEvent(UserActivated event) {
        Message<UserActivated> message = buildMessage(event, "userActivated");
        streamBridge.send(USER_BINDING, message);
    }

    public void sendUserDeactivatedEvent(UserDeactivated event) {
        streamBridge.send(USER_DEACTIVATE_BINDING, event);
    }


    public void sendPasswordReminderTokenGeneratedEvent(PasswordReminderTokenGenerated event) {
        Message<PasswordReminderTokenGenerated> message = buildMessage(event, "tokenGenerated");
        streamBridge.send(USER_BINDING, message);
    }

    private <T> Message<T> buildMessage(T payload, String headerValue) {
        return MessageBuilder.withPayload(payload)
                .setHeader("event_type", headerValue)
                .build();
    }
}
