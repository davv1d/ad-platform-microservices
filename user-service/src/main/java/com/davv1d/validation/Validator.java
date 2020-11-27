package com.davv1d.validation;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator<T> {
    private final T value;
    private final Function<T, T> successFunction;
    private final Set<Condition<T>> softConditions;
    private final Set<Condition<T>> hardConditions;

    private Set<String> getErrors() {
        return checkHardConditions()
                .map(Collections::singleton)
                .orElseGet(this::checkSoftConditions);
    }

    private Optional<String> checkHardConditions() {
        return hardConditions.stream()
                .filter(tCon -> tCon.getPredicate().apply(value))
                .map(Condition::getErrorMessage)
                .findFirst();
    }

    private Set<String> checkSoftConditions() {
        return softConditions.stream()
                .filter(tCon -> tCon.getPredicate().apply(value))
                .map(Condition::getErrorMessage)
                .collect(Collectors.toSet());
    }


    public Result<T> getResult() {
        Set<String> errors = getErrors();
        if (errors.isEmpty()) {
            return Result.success(successFunction.apply(value));
        } else {
            return Result.failure(errors);
        }
    }


    public static final class Builder<T> {
        private T value;
        private Function<T, T> successAction = t -> t;
        private final Set<Condition<T>> softConditions = new HashSet<>();
        private final Set<Condition<T>> hardConditions = new LinkedHashSet<>();

        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }

        public Builder<T> successAction(Function<T, T> successAction) {
            this.successAction = successAction;
            return this;
        }

        public Builder<T> softCondition(Function<T, Boolean> p, String errorMessage) {
            softConditions.add(new Condition<>(errorMessage, p));
            return this;
        }


        public Builder<T> hardCondition(Function<T, Boolean> p, String errorMessage) {
            hardConditions.add(new Condition<>(errorMessage, p));
            return this;
        }

        public Validator<T> build() {
            if (value == null) throw new IllegalStateException("value cannot be null");
            return new Validator<>(value, successAction, softConditions, hardConditions);
        }
    }
}
