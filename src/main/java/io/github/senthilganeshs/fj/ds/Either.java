package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Either<A, B> extends Collection<B> {

    @Override Either<A,B> build (final B value);
    
    <R> R either (final Function<A, R> fa, final Function<B, R> fb);
    
    public static <P, Q> Either <P, Q> left (final P value) {
        return new Left<>(value);
    }
    
    public static <P, Q> Either <P, Q> right (final Q value) {
        return new Right<>(value);
    }

    public static <A, B> Collection<A> lefts (final Collection<Either<A, B>> es) {
        return es.foldl (es.empty(), 
            (rs, t) -> t.either(a -> rs.build(a), b -> rs));
    }
    
    public static <A, B> Collection<B> rights (final Collection<Either<A, B>> es) {
        return es.foldl(es.empty(), 
            (rs, t) -> t.either(a -> rs, b -> rs.build(b)));
    }   

    
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
        public <R> Collection<R> empty() {
            return new Left<>(value);
        }

        @Override
        public <R> R foldl(final R seed, final BiFunction<R, B, R> fn) {
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
        
        @Override
        public String toString() {
            return "Left " + value;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof Left) {
                Left<A, B> lOther = ((Left<A, B>) other);
                return lOther.value.equals(value);
            }
            return false;
        }
    }
    
    final static class Right<A, B> implements Either <A, B> {

        private final B value;

        Right (final B value) {
            this.value = value;
        }
        
        @Override
        public <R> Collection<R> empty() {
            return new Left<>(value);
        }

        @Override
        public <R> R foldl(final R seed, final BiFunction<R, B, R> fn) {
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
        
        @Override
        public String toString() {
            return "Right " + value;
        }
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals (final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof Right) {
                Right<A, B> rOther = ((Right<A,B>) other);
                return rOther.value.equals(value);
            }
            return false;
        }
    }
}