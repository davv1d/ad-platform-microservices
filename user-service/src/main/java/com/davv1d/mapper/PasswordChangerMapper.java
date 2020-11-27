package com.davv1d.mapper;

import com.davv1d.domain.PasswordChanger;
import com.davv1d.domain.dto.PasswordChangerDto;
import org.springframework.stereotype.Component;

@Component
public class PasswordChangerMapper {
    public PasswordChanger mapToPasswordChanger(PasswordChangerDto passwordChangerDto) {
        return new PasswordChanger(passwordChangerDto.getEmail(), passwordChangerDto.getToken(), passwordChangerDto.getNewPassword());
    }
}
