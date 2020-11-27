package com.davv1d.service;

import com.davv1d.domain.PasswordChanger;
import com.davv1d.domain.User;
import com.davv1d.domain.UserRole;
import com.davv1d.domain.dto.PasswordChangerDto;
import com.davv1d.domain.dto.UserDto;
import com.davv1d.mapper.PasswordChangerMapper;
import com.davv1d.mapper.UserMapper;
import com.davv1d.repository.UserRepository;
import com.davv1d.validation.Result;
import com.davv1d.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordChangerMapper passwordChangerMapper;
    private final Validator.Builder<User> creationValidator;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordChangerMapper passwordChangerMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordChangerMapper = passwordChangerMapper;
        this.creationValidator = new Validator.Builder<User>()
                .softCondition(u -> userRepository.existsByEmail(u.getEmail()), "Email exists in database")
                .softCondition(u -> userRepository.existsByName(u.getName()), "Name exists in database")
                .successAction(userRepository::save);
    }

    public Result<User> createUser(UserDto userDto) {
        User user = userMapper.mapToUser(userDto, UserRole.USER);
        return creationValidator
                .value(user)
                .build()
                .getResult();
    }

    public Result<User> createAdmin(UserDto userDto) {
        User user = userMapper.mapToUser(userDto, UserRole.ADMIN);
        return creationValidator
                .value(user)
                .build()
                .getResult();
    }

    public Result<User> activateUser(String activationCode) {
        return Result.of(() -> userRepository.findByActivationToken_Token(activationCode), "Not found code")
                .flatMap(user -> user.activate(activationCode))
                .map(userRepository::save);
    }

    public Result<User> remindPassword(String email) {
        return Result.of(() -> userRepository.findByEmail(email), "User not found")
                .flatMap(user -> user.createPasswordRemindToken("test token 2"))
                .map(userRepository::save);
    }

    public Result<User> deactivateUser(String userId) {
        return Result.of(() -> userRepository.findById(userId), "User not found")
                .flatMap(User::deactivate)
                .map(userRepository::save);
    }

    public Result<User> changePassword(PasswordChangerDto passwordChangerDto) {
        PasswordChanger passwordChanger = passwordChangerMapper.mapToPasswordChanger(passwordChangerDto);
        return Result.of(() -> userRepository.findByEmail(passwordChanger.getEmail()), "User not found")
                .flatMap(user -> user.changePassword(passwordChanger.getNewPassword(), passwordChanger.getToken()))
                .map(userRepository::save);
    }
}
