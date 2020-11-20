package com.davv1d.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordChangerDto {
    private String email;
    private String token;
    private String newPassword;
}
