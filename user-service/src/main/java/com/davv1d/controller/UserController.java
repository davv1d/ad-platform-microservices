package com.davv1d.controller;

import com.davv1d.domain.dto.PasswordChangerDto;
import com.davv1d.domain.dto.UserDto;
import com.davv1d.domain.dto.UserResponseDto;
import com.davv1d.mapper.UserMapper;
import com.davv1d.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/create-user")
    public UserResponseDto createUser(@RequestBody UserDto userDto) {
        return userMapper.mapToUserResponseDto(this.userService.createUser(userDto));
    }

    @PostMapping("/create-admin")
    public UserResponseDto createAdmin(@RequestBody UserDto userDto) {
        return userMapper.mapToUserResponseDto(this.userService.createAdmin(userDto));
    }

    @GetMapping("/activate/{activationCode}")
    public UserResponseDto activateUser(@PathVariable String activationCode) {
        return userMapper.mapToUserResponseDto(this.userService.activateUser(activationCode));
    }

    @PutMapping("/deactivate/{userId}")
    public UserResponseDto deactivateUser(@PathVariable String userId) {
        return userMapper.mapToUserResponseDto(this.userService.deactivateUser(userId));
    }

    @PostMapping("/remind-password/{email}")
    public UserResponseDto remindPassword(@PathVariable String email) {
        return userMapper.mapToUserResponseDto(this.userService.remindPassword(email));
    }

    @PutMapping("/change")
    public UserResponseDto changePassword(@RequestBody PasswordChangerDto passwordChangerDto) {
        return userMapper.mapToUserResponseDto(this.userService.changePassword(passwordChangerDto));
    }
}
