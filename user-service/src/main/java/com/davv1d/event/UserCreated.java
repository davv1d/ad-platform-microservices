package com.davv1d.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserCreated {
    String userId;
    String email;
    String role;
    String status;
    String activationCode;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserCreated(@JsonProperty("userId") String userId,
                       @JsonProperty("email") String email,
                       @JsonProperty("role") String role,
                       @JsonProperty("status") String status,
                       @JsonProperty("activation_code") String activationCode) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.activationCode = activationCode;
    }
}
