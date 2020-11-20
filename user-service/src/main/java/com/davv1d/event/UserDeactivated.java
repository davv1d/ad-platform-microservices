package com.davv1d.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserDeactivated {
    String userId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserDeactivated(@JsonProperty("userId") String userId) {
        this.userId = userId;
    }
}
