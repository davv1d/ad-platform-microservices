package com.davv1d.domain;

import lombok.Value;

@Value
public class PasswordChanger {
    String email;
    String token;
    String newPassword;
}
