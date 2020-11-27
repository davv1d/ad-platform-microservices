package com.davv1d.mapper;

import com.davv1d.domain.User;
import com.davv1d.domain.UserRole;
import com.davv1d.domain.dto.UserDto;
import com.davv1d.domain.dto.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    public User mapToUser(UserDto userDto, UserRole role) {
        String userId = UUID.randomUUID().toString();
        String activationCode = "test123456";
        return new User(userId, userDto.getName(), userDto.getEmail(), userDto.getPassword(), role, activationCode);
    }

    public UserResponseDto mapToUserResponseDto(User user) {
        String activationCode = null;
        String token = null;
        if (user.getActivationToken().isPresent()) {
            activationCode = user.getActivationToken().get().getToken();
        }
        if (user.getPasswordReminderToken().isPresent()) {
            token = user.getPasswordReminderToken().get().getToken();
        }
        return new UserResponseDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                activationCode,
                token);
    }
}
