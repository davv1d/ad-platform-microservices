package com.davv1d.domain.dto;

import com.davv1d.domain.UserRole;
import com.davv1d.domain.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private String userId;
    private String email;
    private UserRole role;
    private UserStatus status;
    private String activationCode;
    private String token;
}
