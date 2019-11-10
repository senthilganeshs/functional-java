package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Iterable<T> {


    <R> Iterable<R> empty();

    Iterable<T> build (final T input);

    <R> R foldLeft (final R seed, final BiFunction<R,T,R> fn);
    
    default <R> Iterable<Iterable<R>> traverse (final Function<T, Iterable<R>> fn) {            
        Iterable<Iterable<R>> seed = empty();
        Iterable<Iterable<R>> sseed = seed.build(empty());
       
        return foldLeft (sseed, (rrs, t) -> fn.apply(t).liftA2((r,  rs) -> rs.build(r), rrs));
    }
    
    static <R> Iterable<Iterable<R>> sequence (final Iterable<Iterable<R>> rs) {
        return rs.traverse(ir -> ir);
    }
    
    default <R> R foldRight (final R seed, final BiFunction<T,R,R> fn) {
        final Function<R, R> res = 
            foldLeft(a -> a,
                (g, t) -> s -> g.apply(fn.apply(t, s)));
        return res.apply(seed);
    }

    default Iterable<T> filter (final Predicate<T> pred) {
        return foldLeft(
            empty(),
            (r, t) -> pred.test(t) ? r.build(t) : r);
    }

    default <R, S> Iterable<S> liftA2 (final Function<T, Function<R, S>> fn, final Iterable<R> rs) {
        return rs.apply(map(fn::apply));        
    }

    default <R, S> Iterable<S> liftA2 (final BiFunction<T, R, S> fn, final Iterable<R> rs) {
        return liftA2(t -> r -> fn.apply(t, r), rs);
    }

    default <R> Iterable<R> map (final Function<T, R> fn) {
        return foldLeft (
            empty(),
            (rs, t) -> rs.build(fn.apply(t)));
    }

    default Iterable<T> concat (final Iterable<T> first) {
        return foldLeft(first, (ts, t) -> ts.build(t));
    }

    default <R> Iterable<R> flatMap (final Function<T, Iterable<R>> fn) {
        return foldLeft (
            empty(),
            (rs, t) -> fn.apply(t).concat(rs));
    }

    default <R> Iterable<R> apply (final Iterable<Function<T, R>> fns) {
        return fns.flatMap(f -> map(f));
    }

    default Iterable<T> forEach (final Consumer<T> action) {
        return foldLeft(this,
            (__, t) -> {
                action.accept(t);
                return null;
            });
    }    
}