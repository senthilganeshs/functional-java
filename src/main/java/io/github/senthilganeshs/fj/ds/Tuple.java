package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;

public interface Tuple<A, B> extends Iterable<B> {

    @Override Tuple<A, B> build (final B input);
    
    Tuple<B, A> swap ();
    
    
    static <A, B> Tuple<A, B> of (final A a, final B b) {
        return new Nil<A, B>().build(b).swap().build(a).swap();
    }
    
    final static class Nil<A, B> implements Tuple<A, B> {

        @Override
        public <R> Iterable<R> empty() {
            return new Nil<>();
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
            return null;
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
    }
}