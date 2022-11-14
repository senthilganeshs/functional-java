package io.github.senthilganeshs.fj.ds;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Set <T extends Comparable<T>> extends Collection<T>, Comparable<T> {

    @Override Set<T> build(final T value);

    default Set<T> build (final T value, final Comparator<T> comparator) {
        return build (value); //discard the comparator.        
    }

    boolean contains(final T value);

    static <R extends Comparable<R>> Collection<R> sort(Collection<R> collection) {
        return of(collection).foldl(collection.empty(), (rs, t) -> rs.build(t));
    }
       
    Set<Integer> EMPTY = new Empty<>();
    
    @SuppressWarnings("unchecked")
    @Deprecated
    static <R extends Comparable<R>> Set<R> nil() {
        return (Set<R>) EMPTY;
    }
    
    static <R extends Comparable<R>> Set<R> of(final Collection<R> values) {
        return values.foldl(nil(), (r, t) -> r.build(t));
    }
    
    static <R extends Comparable<R>> Set<R> of(final java.util.Collection<R> values) {
        Set<R> tree = nil();
        for (final R value : values) {
            tree = tree.build(value);
        }
        return tree;
    }   
    
    @SuppressWarnings("unchecked")
    static <R extends Comparable<R>> Set<R> of(R... values) {
        Set<R> tree = nil();
        if (values == null || values.length == 0)
            return tree;
        
        for (final R value  : values) {
            tree = tree.build(value);            
        }
        return tree;
    }

    interface AVLTree <T extends Comparable<T>> extends Set<T> {
        
        @Override AVLTree<T> build(final T value);

        AVLTree<T> replaceLeft(final Function<AVLTree<T>, AVLTree<T>> left);

        AVLTree<T> replaceRight(final Function<AVLTree<T>, AVLTree<T>> right);

        AVLTree<T> rotateLeft();

        AVLTree<T> rotateRight();

        int height();
    }
   
    final class NonEmpty<T extends Comparable<T>> implements AVLTree<T> {

        private final AVLTree<T> right;
        private final AVLTree<T> left;
        private final T value;

        NonEmpty (final T value, final AVLTree<T> left, final AVLTree<T> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <R> Collection<R> empty() {
            return (Collection<R>) nil();
        }

        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            return right.foldl(
                fn.apply(left.foldl(seed, fn), value), 
                fn);
        }
                
        @Override
        public AVLTree<T> build(T other) {
            if (this.value.compareTo(other) == 0)
                return this;

            AVLTree<T> lf;
            AVLTree<T> rt;
            
            if (this.value.compareTo(other) > 0) {
                lf = this.left.build(other);
                rt = this.right;
            } else {
                lf = this.left;
                rt = right.build(other);
            }

            int lfh = lf.height();
            int rth = rt.height();

            AVLTree<T> newNode =  new NonEmpty<>(value, lf, rt);;

            if (lfh > rth && lfh - rth == 2) {
                if (lf.compareTo(other) > 0) {
                    //single-left-rotation - Ex: [3 [2 [1]]]
                    return newNode.rotateLeft();
                } else {
                    //right-lfet-double rotation - Ex: [3 [1[][2]]]
                    return newNode.replaceLeft(AVLTree::rotateRight).rotateLeft();
                }
            } else if (lfh < rth && rth - lfh == 2) {
                if (rt.compareTo(other) < 0) {
                    //single-right-rotation - Ex: [1 [][2 [][3]]]
                    return newNode.rotateRight();
                } else {
                    //left-right-double rotation - Ex: [1 [][3 [2]]]
                    return newNode.replaceRight(AVLTree::rotateLeft).rotateRight();
                }
            } else {
                return newNode;
            }
        }

        @Override
        public AVLTree<T> replaceLeft(Function<AVLTree<T>, AVLTree<T>> left) {
            return new NonEmpty<>(value, left.apply(this.left), right);
        }

        @Override
        public AVLTree<T> replaceRight(Function<AVLTree<T>, AVLTree<T>> right) {
            return new NonEmpty<>(value, left, right.apply(this.right));
        }

        @Override
        public int compareTo(T other) {
            return this.value.compareTo(other);
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
        public AVLTree<T> rotateLeft() {
            return left.replaceRight(lfrt -> 
                new NonEmpty<>(value, lfrt, right));
        }

        @Override
        public AVLTree<T> rotateRight() {
            return right.replaceLeft(rtlf -> 
                new NonEmpty<>(value, left, rtlf));
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
            if (other instanceof NonEmpty) {
                return ((NonEmpty<T>) other).value.equals(value);
            }
            return false;
        }
    }
    
    class Empty<T extends Comparable<T>> implements AVLTree<T> {

        @SuppressWarnings("unchecked")
        @Override
        public <R> Collection<R> empty() {
            return (Collection<R>) nil();
        }

        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            return seed;
        }

        @SuppressWarnings("unchecked")
        @Override
        public AVLTree<T> build(T value) {
            return new NonEmpty<T>(value, (AVLTree<T>) EMPTY, (AVLTree<T>) EMPTY);
        }
        
        @Override
        public AVLTree<T> replaceLeft(Function<AVLTree<T>, AVLTree<T>> left) {
            return left.apply(this);
        }

        @Override
        public AVLTree<T> replaceRight(Function<AVLTree<T>, AVLTree<T>> right) {
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
        public AVLTree<T> rotateLeft() {
            return this;
        }

        @Override
        public AVLTree<T> rotateRight() {
            return this;
        }

        @Override
        public boolean contains(T value) {
            return false;
        }
    }
}
