package io.github.senthilganeshs.fj.ds;

import java.util.function.BiFunction;

public interface List<T> extends Collection<T> {
    
    @Override List<T> build(final T input);
    
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
        public <R> Collection<R> empty() {
            return nil();
        }

        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
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
        
        @Override
        public boolean equals(final Object other) {
            if (other == null)
                return false;
            if (other == this)
                return true;
            if (other instanceof EmptyList)
                return true;
            return false;
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
        public <R> Collection<R> empty() {
            return nil();
        }
        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            return fn.apply(head.foldl(seed, (r, t) -> fn.apply(r, t)), tail);
        }
        @Override
        public List<T> build(T input) {
            return cons(this, input);
        }

        @Override
        public String toString() {
            return head.foldl("[", (r, t) -> r + t + ",") + tail + "]";
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            
            if (other instanceof LinkedList) {
                LinkedList<T> llOther = (LinkedList<T>) other;
                if (llOther.tail.equals(((LinkedList) other).tail)) {
                    return llOther.head.equals(head);
                }
            }
            return false;
        }
    }
}