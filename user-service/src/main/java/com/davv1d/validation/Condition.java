package com.davv1d.validation;


import lombok.Value;

import java.util.function.Function;

@Value
class Condition<T> {
    String errorMessage;
    Function<T, Boolean> predicate;
}
