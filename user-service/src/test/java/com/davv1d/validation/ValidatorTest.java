package com.davv1d.validation;

import com.davv1d.domain.User;
import org.junit.jupiter.api.Test;

class ValidatorTest {


    @Test
    public void test() {
        Result<User> result = new Validator.Builder<User>()
                .hardCondition(user -> false, "first")
                .hardCondition(user -> false, "second")
                .softCondition(user -> false, "soft 1")
                .softCondition(user -> false, "soft 2")
                .value(new User())
                .build()
                .getResult();

        result.effect(user -> {
            System.out.println(user);
            return "";
        }, errors -> {
            errors.forEach(System.out::println);
            return "";
        });

    }
}
