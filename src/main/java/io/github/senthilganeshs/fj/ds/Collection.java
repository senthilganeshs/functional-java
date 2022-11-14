package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Collection<T> {

    /**
     * Returns empty implementation of this data-structure.
     * 
     * @param <R>
     * @return
     */
    <R> Collection<R> empty();

    /**
     * Builds the data-structure with the input value.
     * 
     * @param input
     * @return
     */
    Collection<T> build (final T input);
    
    
    /**
     * Travel the data-structure in the left to right order.
     * 
     * @param <R>
     * @param seed
     * @param fn
     * @return
     */
    
    <R> R foldl (final R seed, final BiFunction<R,T,R> fn);

    
    
    default <R> R foldr (final R seed, final BiFunction<T,R,R> fn) {
        final Function<R, R> res = 
            foldl(a -> a,
                (g, t) -> s -> g.apply(fn.apply(t, s)));
        return res.apply(seed);
    }

    default <R> Collection<Collection<R>> traverse (final Function<T, Collection<R>> fn) {            
        Collection<Collection<R>> seed = empty();
        Collection<Collection<R>> sseed = seed.build(empty());
       
        return foldl (sseed, (rrs, t) -> fn.apply(t).liftA2((r,  rs) -> rs.build(r), rrs));
    }
   
    default Collection<T> filter (final Predicate<T> pred) {
        return foldl(
            empty(),
            (r, t) -> pred.test(t) ? r.build(t) : r);
    }

    default <R, S> Collection<S> liftA2 (final Function<T, Function<R, S>> fn, final Collection<R> rs) {
        return rs.apply(map(fn::apply));        
    }

    default <R, S> Collection<S> liftA2 (final BiFunction<T, R, S> fn, final Collection<R> rs) {
        return liftA2(t -> r -> fn.apply(t, r), rs);
    }
    
    default <P, Q, R> Collection<R> liftA3 (final Function<T, BiFunction<P, Q, R>> fn, final Collection<P> ps, final Collection<Q> qs) {
        return apply(ps.liftA2((p,  q) -> (t -> fn.apply(t).apply(p, q)), qs));        
    }
    
    default <P, Q, R, S> Collection<S> liftA4 (
        final Function<T, Function<P, BiFunction<Q, R, S>>> fn, 
        final Collection<P> ps, 
        final Collection<Q> qs,
        final Collection<R> rs) {
       
        return apply(
            ps.liftA3(p -> (q, r) -> (t -> fn.apply(t).apply(p).apply(q, r)), qs, rs));
    }

    default <R> Collection<R> map (final Function<T, R> fn) {
        return foldl (
            empty(),
            (rs, t) -> rs.build(fn.apply(t)));
    }

    default Collection<T> concat (final Collection<T> first) {
        return foldl(first, (ts, t) -> ts.build(t));
    }

    default <R> Collection<R> flatMap (final Function<T, Collection<R>> fn) {
        return foldl (
            empty(),
            (rs, t) -> fn.apply(t).concat(rs));
    }

    default <R> Collection<R> apply (final Collection<Function<T, R>> fns) {
        return fns.flatMap(this::map);
    }

    default Collection<T> forEach (final Consumer<T> action) {
        return foldl(this,
            (__, t) -> {
                action.accept(t);
                return this;
            });
    }    

    default int length() {
        return foldl(0, (r, t) -> r + 1);
    }
    
    @SuppressWarnings("unchecked")
    default Collection<T> drop (final int n) {
        Object[] res = new Object[2];
        res[0] = n;
        res[1] = empty();
        return (Collection<T>) foldl (res, 
            (r, t) -> ((Integer) r[0] > 0) ? 
            new Object[] {(Integer) r[0] - 1, r[1]} : 
            new Object[] {(Integer) r[0], ((Collection<T>)r[1]).build(t)})[1];
    }

    default Collection<T> reverse () {
        return foldr (empty(), 
            (t, r) -> r.build(t));
    }
    
    @SuppressWarnings("unchecked")
    default Collection<T> take (final int n) {
        Object[] res = new Object[2];
        res[0] = n;
        res[1] = empty();
        
        return (Collection<T>)foldl(res,
            (r, t) -> ((Integer) r[0] > 0) ?
                new Object[] { (Integer) r[0] - 1, ((Collection<T>)r[1]).build(t)} :
                new Object[] { (Integer) r[0], r[1]})[1];
    }
    
    default Collection<T> intersperse (final T sep) {
        return drop(1).foldl(take(1), (r, t) -> r.build(sep).build(t));
    }

    default Collection<Collection<T>> intercalate(final Collection<Collection<T>> rss) {
        return rss.drop(1).foldl(rss.take(1), (r, t) -> r.build(this).build(t));
    }

    public static <R> Collection<Collection<R>> sequence (final Collection<Collection<R>> rs) {
        return rs.traverse(id -> id);
    }
 
}