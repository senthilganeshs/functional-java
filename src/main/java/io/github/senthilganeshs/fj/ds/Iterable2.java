package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface Iterable2<A, B> {

    <R, S> Iterable2<R, S> empty();
    
    Iterable2 <A, B> build (final A a, final B b);

    <T> T foldl (final T seed, final TriFunction<T,A, B, T> fn);
    
    public static <R> Iterable2<Iterable<R>, Iterable<R>> goForward(final Iterable2<Iterable<R>, Iterable<R>> zipper) {
        return zipper.foldl(
            zipper.empty(), 
               (t, lhs, rhs) -> lhs.unbuild().flatMap(
                (topM, tail) -> t.build(tail, topM.concat(rhs))));
     }
     
     public static <R> Iterable2<Iterable<R>, Iterable<R>> goBack(final Iterable2<Iterable<R>, Iterable<R>> zipper) {
         return zipper.foldl(
             zipper.empty(), 
                (t, lhs, rhs) -> rhs.unbuild().flatMap(
                 (topM, tail) -> t.build(topM.concat(lhs), tail)));
     }
    
    default <T> T foldr (final T seed, final TriFunction<A, B, T, T> fn) {
        final Function<T, T> res = foldl (t -> t,
            (g, a, b) -> s -> g.apply(fn.apply(a, b, s)));
        return res.apply(seed);
    }
    
    default Iterable2<A, B> filter (final BiPredicate<A, B> pred) {
        return foldl (empty(),
            (i2, a, b) -> pred.test(a, b) ? i2.build(a, b) : i2);
    }
    
    default <R, S> Iterable2<R, S> map (final Function<A, R> lfn, final Function<B, S> rfn) {
        return foldl (
            empty(),
            (i2, a, b) -> i2.build(lfn.apply(a), rfn.apply(b)));
    }
    
    default Iterable2<A, B> concat (final Iterable2<A, B> first) {
        return foldl (first, (i2, a, b) -> i2.build(a, b));
    }
    
    default <R, S> Iterable2<R, S> flatMap (final BiFunction<A, B, Iterable2<R, S>> fn) {
        return foldl (
            empty(),
            (i2, a, b) -> fn.apply(a, b).concat(i2));
    }
    
    default <R, S> Iterable2<R, S> apply (final Iterable2<Function<A, R>, Function<B, S>> fns) {
        return fns.flatMap(this::map);
    }
    
    interface TriFunction<A,B,C,R> {
        R apply (final A a, final B b, final C c);
    }
}