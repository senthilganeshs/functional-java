package io.github.senthilganeshs.fj.ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

public interface List<T> extends Iterable<T> {
    
    /**
     * Utility functions
     * 
     */
    
    default int length() {
        return foldLeft(0, (r, t) -> r + 1);
    }
    
    @SuppressWarnings("unchecked")
    default Iterable<T> drop (final int n) {
        Object[] res = new Object[2];
        res[0] = n;
        res[1] = empty();
        return (Iterable<T>) foldLeft (res, 
            (r, t) -> ((Integer) r[0] > 0) ? 
            new Object[] {(Integer) r[0] - 1, r[1]} : 
            new Object[] {(Integer) r[0], ((Iterable<T>)r[1]).build(t)})[1];
    }

    default Iterable<T> reverse () {
        return foldRight (empty(), 
            (t, r) -> r.build(t));
    }
    
    @SuppressWarnings("unchecked")
    default Iterable<T> take (final int n) {
        Object[] res = new Object[2];
        res[0] = n;
        res[1] = empty();
        
        return (Iterable<T>)foldLeft(res,
            (r, t) -> ((Integer) r[0] > 0) ?
                new Object[] { (Integer) r[0] - 1, ((Iterable<T>)r[1]).build(t)} :
                new Object[] { (Integer) r[0], r[1]})[1];
    }
    
    default Iterable<T> intersperse (final T sep) {
        return drop(1).foldLeft(take(1), (r, t) -> r.build(sep).build(t));
    }
    
    static <R> Iterable<List<R>> intercalate (final List<R> rs, final List<List<R>> rss) {
        return rss.drop(1).foldLeft (rss.take(1), (r, t) -> r.build(rs).build(t));
    }
    
    static Boolean and (final Iterable<Boolean> bools) {
        return bools.foldLeft(true, (r, b) -> r && b);
    }
    
    static Boolean or (final Iterable<Boolean> bools) {
        return bools.foldLeft(false, (r, b) -> r || b);
    }
    
    public static <R> List<R> of (final java.util.List<R> list) {
        return new ListIterable<>(list);
    }
    
    @SafeVarargs
    public static <R> List<R> of (final R...values) {
        return new ListIterable<>(Arrays.asList(values));
    }
    
    final static class ListIterable<T> implements List<T> {

        private final java.util.List<T> list;

        ListIterable(final java.util.List<T> list) {
            this.list = list;
        }
        
        @Override
        public <R> Iterable<R> empty() {
            return new ListIterable<>(new ArrayList<>());
        }

        @Override
        public Iterable<T> build(T input) {
            java.util.List<T> newList = new ArrayList<>(list);
            newList.add(input);
            return new ListIterable<>(newList);
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return list.stream().reduce(seed, (r, t) -> fn.apply(r, t), (r1, r2) -> r2);
        }
        
        @Override
        public String toString() {
            return ((List<String>) map(Object::toString)).intersperse(",").foldLeft("[", (r, t) -> r + t) + "]";
        }
    }
}