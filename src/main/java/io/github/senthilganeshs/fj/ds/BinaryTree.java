package io.github.senthilganeshs.fj.ds;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface BinaryTree <T> extends Iterable<T>, Comparable<T> {
    
    @Override
    BinaryTree<T> build(final T value);
    
    default BinaryTree<T> build (final T value, final Comparator<T> comparator) {
        return build (value); //discard the comparator.        
    }
    
    BinaryTree<T> replaceLeft(final Function<BinaryTree<T>, BinaryTree<T>> left);
    
    BinaryTree<T> replaceRight(final Function<BinaryTree<T>, BinaryTree<T>> right);
    
    BinaryTree<T> rotateLeft();
    
    BinaryTree<T> rotateRight();
    
    int height();
    
    boolean contains(final T value);
    
    BinaryTree<Integer> EMPTY = new Empty<>();
    
    @SuppressWarnings("unchecked")
    @Deprecated
    static <R extends Comparable<R>> BinaryTree<R> nil() {
        return (BinaryTree<R>) EMPTY;
    }
    
    static <R> BinaryTree<R> nil(final Comparator<R> comparator) {
        return new Empty<R>() {
            @Override
            public BinaryTree<R> build (final R input) {
                return build(input, comparator);
            }
        };
    }
    
    static <R extends Comparable<R>> BinaryTree<R> of(final Collection<R> values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    static <R extends Comparable<R>> BinaryTree<R> of(final Iterable<R> values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    @SuppressWarnings("unchecked")
    static <R extends Comparable<R>> BinaryTree<R> of(R... values) {
        return of ((r1, r2) -> r1.compareTo(r2), values);
    }
    
    static <R> BinaryTree<R> of(final Comparator<R> comparator, final Iterable<R> values) {
        return values.foldl(nil(comparator), (r, t) -> r.build(t));
    }
    
    static <R> BinaryTree<R> of(final Comparator<R> comparator, final Collection<R> values) {
        BinaryTree<R> tree = nil(comparator);
        for (final R value : values) {
            tree = tree.build(value);
        }
        return tree;
    }
    
    
    @SuppressWarnings("unchecked")
    static <R> BinaryTree<R> of(final Comparator<R> comparator, R... values) {
        BinaryTree<R> tree = nil(comparator);
        if (values == null || values.length == 0)
            return tree;
        
        for (final R value  : values) {
            tree = tree.build(value);            
        }
        return tree;
    }
    
    default List<T> sorted() {
        return foldl (List.nil(), (r, t) -> r.build(t));
    }
    
    final class AVLTree<T> implements BinaryTree<T> {

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

            BinaryTree<T> lf;
            BinaryTree<T> rt;
            
            if (comparator.compare(this.value, other) > 0) {
                lf = this.left.build(other);
                rt = this.right;
            } else {
                lf = this.left;
                rt = right.build(other);
            }

            int lfh = lf.height();
            int rth = rt.height();

            BinaryTree<T> newNode =  new AVLTree<>(value, lf, rt, comparator);;

            if (lfh > rth && lfh - rth == 2) {
                if (lf.compareTo(other) > 0) {
                    //single-left-rotation - Ex: [3 [2 [1]]]
                    return newNode.rotateLeft();
                } else {
                    //right-lfet-double rotation - Ex: [3 [1[][2]]]
                    return newNode.replaceLeft(BinaryTree::rotateRight).rotateLeft();
                }
            } else if (lfh < rth && rth - lfh == 2) {
                if (rt.compareTo(other) < 0) {
                    //single-right-rotation - Ex: [1 [][2 [][3]]]
                    return newNode.rotateRight();
                } else {
                    //left-right-double rotation - Ex: [1 [][3 [2]]]
                    return newNode.replaceRight(BinaryTree::rotateLeft).rotateRight();
                }
            } else {
                return newNode;
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
        public BinaryTree<T> rotateLeft() {
            return left.replaceRight(lfrt -> 
                new AVLTree<>(value, lfrt, right, comparator));
        }

        @Override
        public BinaryTree<T> rotateRight() {
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
    
    class Empty<T> implements BinaryTree<T> {

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
        public BinaryTree<T> rotateLeft() {
            return this;
        }

        @Override
        public BinaryTree<T> rotateRight() {
            return this;
        }

        @Override
        public boolean contains(T value) {
            return false;
        }
    }
}
