package io.github.senthilganeshs.fj.ds;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BinaryTree <T extends Comparable<T>> extends Iterable<T>, Comparable<T> {
    
    @Override public BinaryTree<T> build(final T value);
    
    public BinaryTree<T> replaceLeft (final Function<BinaryTree<T>, BinaryTree<T>> left);
    
    public BinaryTree<T> replaceRight (final Function<BinaryTree<T>, BinaryTree<T>> right);
    
    public BinaryTree<T> swapLeft ();
    
    public BinaryTree<T> swapRight();
    
    public int height();
    
    public boolean contains (final T value);
    
    static BinaryTree<Integer> EMPTY = new Empty<>();
    
    @SuppressWarnings("unchecked")
    public static <R extends Comparable<R>> BinaryTree<R> nil() {
        return (BinaryTree<R>) EMPTY;
    }
    
    public static <R extends Comparable<R>> BinaryTree<R> of (final Collection<R> values) {
        BinaryTree<R> tree = nil();
        for (final R value : values) {
            tree = tree.build(value);
        }
        return tree;
    }
    
    public static <R extends Comparable<R>> BinaryTree<R> of (Iterable<R> values) {
        return values.foldLeft(nil(), (r, t) -> r.build(t));
    }
    
    public default List<T> sorted () {
        return foldLeft (List.nil(), (r, t) -> r.build(t));
    }
    
    @SuppressWarnings("unchecked")
    public static <R extends Comparable<R>> BinaryTree<R> of (R...values) {
        BinaryTree<R> tree = nil();
        if (values == null || values.length == 0)
            return tree;
        
        for (final R value  : values) {
            tree = tree.build(value);            
        }
        return tree;
    }
    
    
    final static class AVLTree<T extends Comparable<T>> implements BinaryTree<T> {

        private final BinaryTree<T> right;
        private final BinaryTree<T> left;
        private final T value;

        AVLTree (final T value, final BinaryTree<T> left, final BinaryTree<T> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <R> Iterable<R> empty() {
            return (Iterable<R>) nil();
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return right.foldLeft(
                fn.apply(left.foldLeft(seed, fn), value), 
                fn);
        }

        @Override
        public BinaryTree<T> build(T other) {
            if (this.value.compareTo(other) == 0)
                return this;
            
            if (this.value.compareTo(other) > 0) {
                BinaryTree<T> lf = left.build(other);
                int lfh = lf.height();
                int rth = right.height();
                
                if (Math.abs(lfh - rth) == 2 ) {
                    if (lf.compareTo(other) > 0) {
                        //single-left-rotation
                        return lf.replaceRight(
                                lfrt -> 
                                new AVLTree<>(this.value, lfrt, right));
                    } else {
                        //left-right-double rotation;
                        return lf.swapRight()
                            .replaceRight(
                                lfrt -> 
                                new AVLTree<>(this.value, lfrt, right));
                        
                    }
                } else {
                    return new AVLTree<>(value, lf, right);
                }
            } else {
                BinaryTree<T> rt = right.build(other);
                int rth = rt.height();
                int lfh = left.height();
                
                if (Math.abs(lfh - rth) == 2) {
                    if (rt.compareTo(other) < 0) {
                        //single-right-rotation
                        return rt.replaceLeft(rtlf -> 
                            new AVLTree<>(value, left, rtlf));
                    } else {
                        //right-left-rotation
                        return rt.swapLeft()
                            .replaceLeft(rtlf ->
                            new AVLTree<>(value, left, rtlf));
                    }
                } else {
                    return new AVLTree<>(value, left, rt);
                }
            }
        }

        @Override
        public BinaryTree<T> replaceLeft(Function<BinaryTree<T>, BinaryTree<T>> left) {
            return new AVLTree<>(value, left.apply(this.left), right);
        }

        @Override
        public BinaryTree<T> replaceRight(Function<BinaryTree<T>, BinaryTree<T>> right) {
            return new AVLTree<>(value, left, right.apply(this.right));
        }

        @Override
        public int compareTo(T other) {
            return value.compareTo(other);
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
                new AVLTree<>(value, lfrt, right));
        }

        @Override
        public BinaryTree<T> swapRight() {
            return right.replaceLeft(rtlf -> 
                new AVLTree<>(value, left, rtlf));            
        }

        @Override
        public boolean contains(T value) {
            if (this.value.compareTo(value) == 0)
                return true;
            if (this.value.compareTo(value) > 0)
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
    
    final static class Empty<T extends Comparable<T>> implements BinaryTree<T> {

        @SuppressWarnings("unchecked")
        @Override
        public <R> Iterable<R> empty() {
            return (Iterable<R>) nil();
        }

        @Override
        public <R> R foldLeft(R seed, BiFunction<R, T, R> fn) {
            return seed;
        }

        @Override
        public BinaryTree<T> build(T value) {
            return new AVLTree<>(value, nil(), nil());
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