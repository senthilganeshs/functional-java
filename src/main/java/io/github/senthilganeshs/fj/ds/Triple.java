package io.github.senthilganeshs.fj.ds;

public interface Triple<A, B, C> extends Iterable3<A, B, C> {

    @Override Triple<A, B, C> build (final A a, final B b, final C c);
    
    static Triple<Void, Void, Void> EMPTY = new Nil<>();
    
    @SuppressWarnings("unchecked")
    public static <A, B, C> Triple <A, B, C> nil() {
        return (Triple<A, B, C>) EMPTY;
    }
    
    public static <A, B, C> Triple <A, B, C> of (final A a, final B b, final C c) {
        return new Simple<>(a, b, c);
    }
    
    final static class Nil<A, B, C> implements Triple <A, B, C> {

        @Override
        public <R, S, T> Iterable3<R, S, T> empty() {
            return nil();
        }

        @Override
        public <T> T foldl(T seed, QuadFunction<T, A, B, C, T> fn) {
            return seed;
        }

        @Override
        public Triple<A, B, C> build(A a, B b, C c) {
            return new Simple<>(a, b, c);
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
    
    final static class Simple <A, B, C> implements Triple <A, B, C> {
        
        private final C c;
        private final B b;
        private final A a;

        Simple (final A a, final B b, final C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public <R, S, T> Iterable3<R, S, T> empty() {
            return nil();
        }

        @Override
        public <T> T foldl(T seed, QuadFunction<T, A, B, C, T> fn) {
            return fn.apply(seed, a, b, c);
        }

        @Override
        public Triple<A, B, C> build(A a, B b, C c) {
            return new Simple<>(a, b, c);
        }  
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof Simple) {
                Simple<A, B, C> sOther = (Simple <A, B, C>) other;
                if (sOther.a.equals(a)) {
                    if(sOther.b.equals(b)) {
                        return sOther.c.equals(c);
                    }
                }                
            }
            return false;
        }
        
        @Override
        public String toString() {
            return String.format("(%s, %s, %s)", a, b, c);
        }
    }
}
