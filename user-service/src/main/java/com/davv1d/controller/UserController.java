package com.davv1d.controller;

import com.davv1d.domain.dto.PasswordChangerDto;
import com.davv1d.domain.dto.UserDto;
import com.davv1d.mapper.UserMapper;
import com.davv1d.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        return this.userService.createUser(userDto).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody UserDto userDto) {
        return this.userService.createAdmin(userDto).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @GetMapping("/activate/{activationCode}")
    public ResponseEntity<?> activateUser(@PathVariable String activationCode) {
        return this.userService.activateUser(activationCode).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<?> deactivateUser(@PathVariable String userId) {
        return this.userService.deactivateUser(userId).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @PostMapping("/remind-password/{email}")
    public ResponseEntity<?> remindPassword(@PathVariable String email) {
        return this.userService.remindPassword(email).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @PutMapping("/change")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangerDto passwordChangerDto) {
        return this.userService.changePassword(passwordChangerDto).effect(
                user -> ResponseEntity.ok(userMapper.mapToUserResponseDto(user)),
                errors -> ResponseEntity.badRequest().body(errors));
    }

    @RequestMapping(value = { "/user" }, produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }
}
