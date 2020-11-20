package com.davv1d.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PasswordReminderTokenGenerated {
    String userId;
    String token;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PasswordReminderTokenGenerated(@JsonProperty("userId") String userId,
                                          @JsonProperty("token") String token) {
        this.userId = userId;
        this.token = token;
    }
}
