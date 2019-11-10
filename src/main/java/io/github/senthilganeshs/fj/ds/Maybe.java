package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;


public interface Maybe<T> extends Iterable<T> {

    final static Maybe<Void> NOTHING = new Nothing<>();

    @SuppressWarnings("unchecked")
    public static <R> Maybe<R> nothing () {
        return (Maybe<R>) NOTHING;
    }

    public static <R> Maybe<R> some (final R value) {
        return new Some<>(value);
    }
    final static class Nothing<T> implements Maybe<T> {

        @Override
        public <R> Maybe<R> empty() {
            return Maybe.nothing();
        }

        @Override
        public Maybe<T> build(T input) {
            return Maybe.some(input);
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return seed;
        }

        @Override
        public String toString() {
            return "Nothing";
        }
    }

    final static class Some<T> implements Maybe<T> {

        private T value;

        Some (final T value) {
            this.value = value;
        }

        @Override
        public <R> Maybe<R> empty() {
            return Maybe.nothing();
        }

        @Override
        public Maybe<T> build(T input) {
            return new Some<>(input);
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return fn.apply(seed, value);
        }

        @Override
        public String toString() {
            return "Some (" + value + ")";
        }
    }
}