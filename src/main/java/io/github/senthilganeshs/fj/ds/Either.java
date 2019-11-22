package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Either<A, B> extends Iterable<B> {

    @Override Either<A,B> build (final B value);
    
    <R> R either (final Function<A, R> fa, final Function<B, R> fb);
    
    default B fromRight (final B def) {
        return either (a -> def, b -> b);
    }
    
    default A fromLeft (final A def) {
        return either(a -> a, b -> def);
    }
    
    default boolean isLeft () {
        return either (a -> true, b -> false);
    }
    
    default boolean isRight() {
        return either (a -> false, b -> true);
    }
    
    final static class Left <A, B> implements Either <A, B> {
        private final A value;

        Left (final A value) {
            this.value = value;
        }

        @Override
        public <R> Iterable<R> empty() {
            return new Left<>(value);
        }

        @Override
        public <R> R foldLeft(final R seed, final BiFunction<R, B, R> fn) {
            return seed;
        }

        @Override
        public Either<A, B> build(final B value) {
            return new Right<>(value);
        }

        @Override
        public <R> R either(Function<A, R> fa, Function<B, R> fb) {
            return fa.apply(value);
        }
    }
    
    final static class Right<A, B> implements Either <A, B> {

        private final B value;

        Right (final B value) {
            this.value = value;
        }
        
        @Override
        public <R> Iterable<R> empty() {
            return new Left<>(value);
        }

        @Override
        public <R> R foldLeft(final R seed, final BiFunction<R, B, R> fn) {
            return fn.apply(seed, value);
        }

        @Override
        public Either<A, B> build(final B value) {
            //lose the old value.
            return new Right<>(value);
        }

        @Override
        public <R> R either(Function<A, R> fa, Function<B, R> fb) {
            return fb.apply(value);
        }        
    }
}
