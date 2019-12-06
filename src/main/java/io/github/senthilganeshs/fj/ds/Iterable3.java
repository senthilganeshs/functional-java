package io.github.senthilganeshs.fj.ds;

import java.util.function.Function;

import io.github.senthilganeshs.fj.ds.Iterable2.TriFunction;

public interface Iterable3 <A, B, C> {
    
    <R, S, T> Iterable3<R, S, T> empty();
    
    Iterable3<A, B, C> build (final A a, final B b, final C c);
    
    <T> T foldl (final T seed, final QuadFunction<T, A, B, C, T> fn);
    
    default <T> T foldr (final T seed, final QuadFunction<A,B,C,T,T> fn) {
        final Function<T, T> res = foldl (t -> t,
            (g, a, b, c) -> s -> g.apply(fn.apply(a, b, c, s)));
        return res.apply(seed);
    }
    
    default Iterable3<A, B, C> filter (final TriPredicate<A, B, C> pred) {
        return foldl (empty(),
            (i2, a, b, c) -> pred.test(a, b, c) ? i2.build(a, b, c) : i2);
    }
    
    default <R, S, T> Iterable3<R, S, T> map (final Function<A, R> afn, final Function<B, S> bfn, final Function<C, T> cfn) {
        return foldl (
            empty(),
            (i2, a, b, c) -> i2.build(afn.apply(a), bfn.apply(b), cfn.apply(c)));
    }
    
    default Iterable3<A, B, C> concat (final Iterable3<A, B, C> first) {
        return foldl (first, (i2, a, b, c) -> i2.build(a, b, c));
    }
    
    default <R, S, T> Iterable3<R, S, T> flatMap (final TriFunction<A, B, C, Iterable3<R, S, T>> fn) {
        return foldl (
            empty(),
            (i2, a, b, c) -> fn.apply(a, b, c).concat(i2));
    }
    
    default <R, S, T> Iterable3<R, S, T> apply (final Iterable3<Function<A, R>, Function<B, S>, Function<C, T>> fns) {
        return fns.flatMap(this::map);
    }
    
    
    @FunctionalInterface
    interface QuadFunction<A,B,C,D,X> {
        X apply (final A a, final B b, final C c, final D d);
    }
    
    @FunctionalInterface
    interface TriPredicate<A, B, C> {
        boolean test (final A a, final B b, final C c);
    }
}