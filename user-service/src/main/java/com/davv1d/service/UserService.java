package com.davv1d.service;

import com.davv1d.domain.User;
import com.davv1d.domain.UserRole;
import com.davv1d.domain.dto.PasswordChangerDto;
import com.davv1d.domain.dto.UserDto;
import com.davv1d.mapper.UserMapper;
import com.davv1d.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createUser(UserDto userDto) {
        return userRepository.save(userMapper.mapToUser(userDto, UserRole.USER));
    }

    public User createAdmin(UserDto userDto) {
        return userRepository.save(userMapper.mapToUser(userDto, UserRole.ADMIN));
    }

    public User activateUser(String activationCode) {
        User user = userRepository.findByActivationToken_Token(activationCode).orElseThrow(() -> new IllegalStateException("Incorrect activation code"));
        user.activate(activationCode);
        return userRepository.save(user);
    }

    public User remindPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User does not exist"));
        user.createPasswordRemindToken("test token 2");
        return userRepository.save(user);
    }

    public User deactivateUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User does not exist"));
        user.deactivate();
        return userRepository.save(user);
    }

    public User changePassword(PasswordChangerDto passwordChangerDto) {
        User user = userRepository.findByEmail(passwordChangerDto.getEmail()).orElseThrow(() -> new IllegalStateException("User does not exist"));
        user.changePassword(passwordChangerDto.getNewPassword(), passwordChangerDto.getToken());
        return userRepository.save(user);
    }
}
