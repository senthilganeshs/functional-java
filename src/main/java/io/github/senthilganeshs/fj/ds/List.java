package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;

public interface List<T> extends Iterable<T> {
    
    @Override List<T> build(final T input);
    
    static <R extends Comparable<R>> Iterable<R> sort (final Iterable<R> iter) {
        return BinaryTree.of(iter).sorted();
    }
    
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
        return list.stream().reduce(nil(), (rs, r) ->rs.build(r), (r1, r2) -> r2);
    }
    
    @SafeVarargs
    public static <R> List<R> of (final R...values) {
        if (values == null || values.length == 0)
            return nil();
        List<R> list = nil();
        for (R value : values) {
            list = list.build(value);
        }
        return list;
    }
    
    static final List<Void> EMPTY = new EmptyList<Void>();
    
    @SuppressWarnings("unchecked")
    public static<R> List<R> nil() {
        return (List<R>) EMPTY;
    }
    
    public static <R> List<R> cons(final List<R> head, final R tail) {
        return new LinkedList<>(head, tail);
    }
    
    final static class EmptyList<T> implements List<T> {

        @Override
        public <R> Iterable<R> empty() {
            return nil();
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return seed;
        }

        @Override
        public List<T> build(T input) {
            return new LinkedList<>(this, input);
        }
        
        @Override
        public String toString() {
            return "";
        }
        
    }
    
    final static class LinkedList<T> implements List<T> {
        private final T tail;
        private final List<T> head;
        
        LinkedList (final List<T> head, final T tail) {
            this.head = head;
            this.tail = tail;
        }
        
        @Override
        public <R> Iterable<R> empty() {
            return nil();
        }
        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return fn.apply(head.foldLeft(seed, (r, t) -> fn.apply(r, t)), tail);
        }
        @Override
        public List<T> build(T input) {
            return cons(this, input);
        }
        @Override
        public String toString() {
            return head.foldLeft("[", (r, t) -> r + t + ",") + tail + "]";
        }
    }
}