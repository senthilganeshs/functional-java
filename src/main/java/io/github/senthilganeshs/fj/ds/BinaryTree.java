package io.github.senthilganeshs.fj.ds;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BinaryTree <T> extends Iterable<T>, Comparable<T> {
    
    @Override public BinaryTree<T> build(final T value);
    
    default BinaryTree<T> build (final T value, final Comparator<T> comparator) {
        return build (value); //discard the comparator.        
    }
    
    public BinaryTree<T> replaceLeft (final Function<BinaryTree<T>, BinaryTree<T>> left);
    
    public BinaryTree<T> replaceRight (final Function<BinaryTree<T>, BinaryTree<T>> right);
    
    public BinaryTree<T> swapLeft ();
    
    public BinaryTree<T> swapRight();
    
    public int height();
    
    public boolean contains (final T value);
    
    static BinaryTree<Integer> EMPTY = new Empty<>();
    
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <R extends Comparable<R>> BinaryTree<R> nil() {
        return (BinaryTree<R>) EMPTY;
    }
    
    public static <R> BinaryTree<R> nil (final Comparator<R> comparator) {
        return new Empty<R>() {
            @Override
            public BinaryTree<R> build (final R input) {
                return build(input, comparator);
            }
        };
    }
    
    public static <R extends Comparable<R>> BinaryTree<R> of (final Collection<R> values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    public static <R extends Comparable<R>> BinaryTree<R> of (final Iterable<R> values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    @SuppressWarnings("unchecked")
    public static <R extends Comparable<R>> BinaryTree<R> of (R...values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    public static <R> BinaryTree<R> of (final Comparator<R> comparator, final Iterable<R> values) {
        return values.foldl(nil(comparator), (r, t) -> r.build(t));
    }
    
    public static <R> BinaryTree<R> of (final Comparator<R> comparator, final Collection<R> values) {
        BinaryTree<R> tree = nil(comparator);
        for (final R value : values) {
            tree = tree.build(value);
        }
        return tree;
    }
    
    
    @SuppressWarnings("unchecked")
    public static <R> BinaryTree<R> of (final Comparator<R> comparator, R...values) {
        BinaryTree<R> tree = nil(comparator);
        if (values == null || values.length == 0)
            return tree;
        
        for (final R value  : values) {
            tree = tree.build(value);            
        }
        return tree;
    }
    
    public default List<T> sorted () {
        return foldl (List.nil(), (r, t) -> r.build(t));
    }
    
    final static class AVLTree<T> implements BinaryTree<T> {

        private final BinaryTree<T> right;
        private final BinaryTree<T> left;
        private final T value;
        private final Comparator<T> comparator;

        AVLTree (final T value, final BinaryTree<T> left, final BinaryTree<T> right, Comparator<T> comparator) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.comparator = comparator;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <R> Iterable<R> empty() {
            return (Iterable<R>) nil();
        }

        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            return right.foldl(
                fn.apply(left.foldl(seed, fn), value), 
                fn);
        }
        
        @Override
        public Iterable2<Maybe<T>, Iterable<T>> unbuild () {
            Maybe<T> succ = left.foldl(Maybe.nothing(), (r, t) -> Maybe.some(t));
            return Tuple.of(Maybe.some(value), succ.concat(left).concat(right));
        }
        
        @Override
        public BinaryTree<T> build(T other) {
            if (comparator.compare(this.value, other) == 0)
                return this;
            
            if (comparator.compare(this.value, other) > 0) {
                BinaryTree<T> lf = left.build(other);
                int lfh = lf.height();
                int rth = right.height();
                
                if (Math.abs(lfh - rth) == 2 ) {
                    if (lf.compareTo(other) > 0) {
                        //single-left-rotation
                        return lf.replaceRight(
                                lfrt -> 
                                new AVLTree<>(this.value, lfrt, right, comparator));
                    } else {
                        //left-right-double rotation;
                        return lf.swapRight()
                            .replaceRight(
                                lfrt -> 
                                new AVLTree<>(this.value, lfrt, right, comparator));
                        
                    }
                } else {
                    return new AVLTree<>(value, lf, right, comparator);
                }
            } else {
                BinaryTree<T> rt = right.build(other);
                int rth = rt.height();
                int lfh = left.height();
                
                if (Math.abs(lfh - rth) == 2) {
                    if (rt.compareTo(other) < 0) {
                        //single-right-rotation
                        return rt.replaceLeft(rtlf -> 
                            new AVLTree<>(value, left, rtlf, comparator));
                    } else {
                        //right-left-rotation
                        return rt.swapLeft()
                            .replaceLeft(rtlf ->
                            new AVLTree<>(value, left, rtlf, comparator));
                    }
                } else {
                    return new AVLTree<>(value, left, rt, comparator);
                }
            }
        }

        @Override
        public BinaryTree<T> replaceLeft(Function<BinaryTree<T>, BinaryTree<T>> left) {
            return new AVLTree<>(value, left.apply(this.left), right, comparator);
        }

        @Override
        public BinaryTree<T> replaceRight(Function<BinaryTree<T>, BinaryTree<T>> right) {
            return new AVLTree<>(value, left, right.apply(this.right), comparator);
        }

        @Override
        public int compareTo(T other) {
            return comparator.compare(this.value, other);
        }

        @Override
        public int height() {
            return 1 + Math.max(left.height(), right.height());
        }
        
        @Override
        public String toString() {
            return String.format(
                "{ Label : %s, left = %s, right = %s }",
                value, left, right);
        }

        @Override
        public BinaryTree<T> swapLeft() {
            return left.replaceRight(lfrt -> 
                new AVLTree<>(value, lfrt, right, comparator));
        }

        @Override
        public BinaryTree<T> swapRight() {
            return right.replaceLeft(rtlf -> 
                new AVLTree<>(value, left, rtlf, comparator));
        }

        @Override
        public boolean contains(T value) {
            if (comparator.compare(this.value, value) == 0)
                return true;
            if (comparator.compare(this.value, value) > 0)
                return left.contains(value);
            return right.contains(value);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object other) {
            if (other == null) return false;
            if (other == this) return true;
            if (other instanceof AVLTree) {
                return ((AVLTree<T>) other).value.equals(value);
            }
            return false;
        }
    }
    
    static class Empty<T> implements BinaryTree<T> {

        @SuppressWarnings("unchecked")
        @Override
        public <R> Iterable<R> empty() {
            return (Iterable<R>) nil();
        }

        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            return seed;
        }

        @SuppressWarnings("unchecked")
        @Override
        public BinaryTree<T> build(T value) {
            if (value instanceof Comparable) {
                return build(value, (t1, t2) -> ((Comparable) t1).compareTo(t2));
            }
            throw new IllegalArgumentException(value + " is not Comparable. Use build with comparator api");
        }
        
        @Override
        public BinaryTree<T> build (final T value, final Comparator<T> comparator) {
            return new AVLTree<>(value, nil(comparator), nil(comparator), comparator);
        }

        @Override
        public BinaryTree<T> replaceLeft(Function<BinaryTree<T>, BinaryTree<T>> left) {
            return left.apply(this);
        }

        @Override
        public BinaryTree<T> replaceRight(Function<BinaryTree<T>, BinaryTree<T>> right) {
            return right.apply(this);
        }

        @Override
        public int compareTo(T o) {
            return -1;
        }

        @Override
        public int height() {
            return 0;
        } 
        
        @Override
        public String toString() {
            return "[]";
        }

        @Override
        public BinaryTree<T> swapLeft() {
            return this;
        }

        @Override
        public BinaryTree<T> swapRight() {
            return this;
        }

        @Override
        public boolean contains(T value) {
            return false;
        }
    }
}
