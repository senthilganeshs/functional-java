package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Iterable<T> {

    /**
     * Returns empty implementation of this data-structure.
     * 
     * @param <R>
     * @return
     */
    <R> Iterable<R> empty();

    /**
     * Returns same data-structure after accepting input value.
     * 
     * @param input
     * @return
     */
    Iterable<T> build (final T input);
    
    
    /**
     * Travel the data-structure in the left to right order.
     * 
     * @param <R>
     * @param seed
     * @param fn
     * @return
     */
    
    <R> R foldl (final R seed, final BiFunction<R,T,R> fn);
    
    default Iterable2<Maybe<T>, Iterable<T>> unbuild() {
        return Tuple.of(Maybe.nothing(), empty());
    }
    
    
    /*
     * BEGIN of default and static methods. 
     * */
    
    default <R> R foldr (final R seed, final BiFunction<T,R,R> fn) {
        final Function<R, R> res = 
            foldl(a -> a,
                (g, t) -> s -> g.apply(fn.apply(t, s)));
        return res.apply(seed);
    }
    
    public static <R extends Comparable<R>> Iterable<R> sort (final Iterable<R> iter) {
        return BinaryTree.of(iter).sorted();
    }
   
    public static <A, B> Iterable<A> lefts (final Iterable<Either<A, B>> es) {
        return es.foldl (es.empty(), 
            (rs, t) -> t.either(a -> rs.build(a), b -> rs));
    }
    
    public static <A, B> Iterable<B> rights (final Iterable<Either<A, B>> es) {
        return es.foldl(es.empty(), 
            (rs, t) -> t.either(a -> rs, b -> rs.build(b)));
    }
    
    public static <A, B> Tuple<Iterable<A>, Iterable<B>> partition (final Iterable<Either<A, B>> es) {
        return Tuple.of(lefts(es), rights(es));
    }

    public static <R> Iterable<Iterable<R>> sequence (final Iterable<Iterable<R>> rs) {
        return rs.traverse(id -> id);
    }
 
    default Iterable2<Iterable<T>, Iterable<T>> zipper () {
        return Tuple.of(this, empty());
    }
    
    default <R> Iterable<Iterable<R>> traverse (final Function<T, Iterable<R>> fn) {            
        Iterable<Iterable<R>> seed = empty();
        Iterable<Iterable<R>> sseed = seed.build(empty());
       
        return foldl (sseed, (rrs, t) -> fn.apply(t).liftA2((r,  rs) -> rs.build(r), rrs));
    }
   
    default Iterable<T> filter (final Predicate<T> pred) {
        return foldl(
            empty(),
            (r, t) -> pred.test(t) ? r.build(t) : r);
    }

    default <R, S> Iterable<S> liftA2 (final Function<T, Function<R, S>> fn, final Iterable<R> rs) {
        return rs.apply(map(fn::apply));        
    }

    default <R, S> Iterable<S> liftA2 (final BiFunction<T, R, S> fn, final Iterable<R> rs) {
        return liftA2(t -> r -> fn.apply(t, r), rs);
    }
    
    default <P, Q, R> Iterable<R> liftA3 (final Function<T, BiFunction<P, Q, R>> fn, final Iterable<P> ps, final Iterable<Q> qs) {
        return apply(ps.liftA2((p,  q) -> (t -> fn.apply(t).apply(p, q)), qs));        
    }
    
    default <P, Q, R, S> Iterable<S> liftA4 (
        final Function<T, Function<P, BiFunction<Q, R, S>>> fn, 
        final Iterable<P> ps, 
        final Iterable<Q> qs,
        final Iterable<R> rs) {
       
        return apply(
            ps.liftA3(p -> (q, r) -> (t -> fn.apply(t).apply(p).apply(q, r)), qs, rs));
    }

    default <R> Iterable<R> map (final Function<T, R> fn) {
        return foldl (
            empty(),
            (rs, t) -> rs.build(fn.apply(t)));
    }

    default Iterable<T> concat (final Iterable<T> first) {
        return foldl(first, (ts, t) -> ts.build(t));
    }

    default <R> Iterable<R> flatMap (final Function<T, Iterable<R>> fn) {
        return foldl (
            empty(),
            (rs, t) -> fn.apply(t).concat(rs));
    }

    default <R> Iterable<R> apply (final Iterable<Function<T, R>> fns) {
        return fns.flatMap(this::map);
    }

    default Iterable<T> forEach (final Consumer<T> action) {
        return foldl(this,
            (__, t) -> {
                action.accept(t);
                return this;
            });
    }    
}