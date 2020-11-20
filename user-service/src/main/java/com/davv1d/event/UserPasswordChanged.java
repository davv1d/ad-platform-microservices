package com.davv1d.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserPasswordChanged {
    String userId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserPasswordChanged(@JsonProperty("userId") String userId) {
        this.userId = userId;
    }
}
