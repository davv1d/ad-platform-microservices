package com.davv1d.validation;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Result<V> implements Serializable {
    private Result() {
    }

    public abstract Set<String> getErrorsMessages();

    public abstract boolean effect(Consumer<V> c);

    public abstract <U> U effect(Function<V, U> success, Function<Set<String>, U> failure);

    public abstract V getOrElse(Supplier<V> c);

    public abstract <U> Result<U> map(Function<V, U> f);

    public abstract <U> Result<U> flatMap(Function<V, Result<U>> f);

    public abstract void forEach(Consumer<V> success, Consumer<Set<String>> failure);

    public abstract void errorEffect(Consumer< Set<String>> errorMessage);

    public static class Failure<V> extends Result<V> {
        private final Set<String> message = new HashSet<>();

        private Failure(Set<String> message) {
            this.message.addAll(message);
        }

        @Override
        public Set<String> getErrorsMessages() {
            return message;
        }

        @Override
        public boolean effect(Consumer<V> c) {
            return false;
        }

        @Override
        public <U> U effect(Function<V, U> success, Function<Set<String>, U> failure) {
            return failure.apply(message);
        }

        @Override
        public V getOrElse(Supplier<V> c) {
            return c.get();
        }

        @Override
        public <U> Result<U> map(Function<V, U> f) {
            return failure(message);
        }

        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            return failure(message);
        }

        @Override
        public void forEach(Consumer<V> success, Consumer<Set<String>> failure) {
            failure.accept(message);
        }

        @Override
        public void errorEffect(Consumer< Set<String>> c) {
            c.accept(message);
        }
    }

    public static class Success<V> extends Result<V> {
        private final V value;

        private Success(V value) {
            super();
            this.value = value;
        }


        @Override
        public Set<String> getErrorsMessages() {
            return Collections.emptySet();
        }

        @Override
        public boolean effect(Consumer<V> c) {
            c.accept(value);
            return true;
        }

        @Override
        public <U> U effect(Function<V, U> success, Function<Set<String>, U> failure) {
            return success.apply(value);
        }

        @Override
        public V getOrElse(Supplier<V> c) {
            return value;
        }

        @Override
        public <U> Result<U> map(Function<V, U> f) {
            try {
                return success(f.apply(value));
            } catch (Exception e) {
                return failure(Collections.singleton(e.getMessage()));
            }
        }

        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            try {
                return f.apply(value);
            } catch (Exception e) {
                return failure(Collections.singleton(e.getMessage()));
            }
        }

        @Override
        public void forEach(Consumer<V> success, Consumer<Set<String>> failure) {
            success.accept(value);
        }

        @Override
        public void errorEffect(Consumer< Set<String>> errorMessage) {
        }
    }

    public static <V> Result<V> failure(Set<String> message) {
        return new Failure<>(message);
    }

    public static <V> Result<V> failure(String message) {
        return new Failure<>(Collections.singleton(message));
    }


    public static <V> Result<V> success(V value) {
        return new Success<>(value);
    }


    public static <V> Result<V> of(Supplier<Optional<V>> s, String errorMessage) {
        Optional<V> v = s.get();
        if (v.isPresent()) {
            return new Success<>(v.get());
        } else {
            return new Failure<>(Collections.singleton(errorMessage));
        }
    }
}
