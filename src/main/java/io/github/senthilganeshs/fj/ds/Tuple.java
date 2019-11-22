package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;

public interface Tuple<A, B> extends Iterable<B> {

    @Override Tuple<A, B> build (final B input);
    
    Tuple<B, A> swap ();
    
    static Tuple<Void, Void> EMPTY = new Nil<>();
    
    @SuppressWarnings("unchecked")
    static <A, B> Tuple<A, B> nil() {
        return (Tuple<A, B>) EMPTY;
    }
    
    static <A, B> Tuple<A, B> of (final A a, final B b) {
        return new Simple<>(a, b);
    }
    
    final static class Nil<A, B> implements Tuple<A, B> {

        @Override
        public <R> Iterable<R> empty() {
            return nil();
        }

        @Override
        public <R> R foldLeft(final R seed, final BiFunction<R, B, R> fn) {
            return seed;
        }

        @Override
        public Tuple<A, B> build(final B input) {
            return new Simple <>(null, input);
        }

        @Override
        public Tuple<B, A> swap() {
            return new Nil<>();
        }
        @Override
        public String toString() {
            return "()";
        }
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof Nil) {
                return true;                
            }
            return false;
        }
    }
    
    final static class Simple<A, B> implements Tuple<A, B> {

        private final B b;
        private final A a;

        Simple (final A a, final B b) {
            this.a = a;
            this.b = b;
        }
        
        @Override
        public <R> Iterable<R> empty() {
            return new Nil<>();
        }

        @Override
        public <R> R foldLeft(final R seed, final BiFunction<R, B, R> fn) {
            return fn.apply(seed, b);
        }

        @Override
        public Tuple<A, B> build(final B input) {
            //discard the old value
            return new Simple<>(a, input);
        }

        @Override
        public Tuple<B, A> swap() {
            return new Simple<>(b, a);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof Simple) {
                Simple<A, B> sOther = (Simple <A, B>) other;
                if(sOther.b.equals(b)) {
                    if (sOther.a == null && a == null)
                        return true;
                    return sOther.a.equals(a);
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return String.format("(%s, %s)", a, b);
        }
    }
}