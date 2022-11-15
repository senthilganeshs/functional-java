package io.github.senthilganeshs.fj.ds;

import java.util.Arrays;
import java.util.function.BiFunction;

public interface Array<T> extends Collection<T> {

    public Maybe<T> at(int index);

    public Maybe<T> remove(int index);

    public Array<T> shift();

    final static class NonEmpty<T> implements Array<T> {

        private Object[] values;
        private int cursor;
        private int capacity;
        private final int initialCapacity;

        NonEmpty(int capacity) {
            this.initialCapacity = capacity;
            this.capacity = capacity;
            this.values = new Object[capacity];
            this.cursor = 0;
        }

        @Override
        public <R> Collection<R> empty() {
            return new NonEmpty<>(this.values.length);
        }

        @Override
        public Collection<T> build(T input) {
            if (cursor == this.values.length - 1) {
                this.capacity += capacity;
                this.values = Arrays.copyOf(this.values, capacity);
            }

            values[cursor + 1] = input;
            cursor ++;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> R foldl(R seed, BiFunction<R, T, R> fn) {
            R res = seed;
            for (int i = 0 ; i <= cursor; i++) {
               res = fn.apply(res, (T) values[i]);     
            }
            return res;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Maybe<T> at(int index) {
            if (index < 0 || index >= capacity || index > cursor) {
                return Maybe.nothing();
            }
            return Maybe.some((T) values[index]);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Maybe<T> remove(int index) {
            if (index < 0 || index >= capacity || index > cursor) {
                return Maybe.nothing();
            }
            T atIndex = (T) values[index];
            for (int i = index; i < cursor; i++) {
                this.values[i] = this.values[i + 1];
            }
            this.cursor --;
            if (cursor < capacity / 2 && capacity > initialCapacity) {
                this.capacity = this.capacity / 2;
                if (this.capacity < initialCapacity) {
                    this.capacity = initialCapacity;
                }
                this.values = Arrays.copyOf(this.values, capacity / 2);
            }
            return Maybe.some(atIndex);
        }

        @Override
        public Array<T> shift() {
            if(cursor == 0) {
                return this; // Can't shift empty array.
            }
            this.values = Arrays.copyOfRange(this.values, 1, this.cursor);
            this.cursor --;
            if (cursor < capacity / 2 && capacity > initialCapacity) {
                this.capacity = this.capacity / 2;
                if (this.capacity < initialCapacity) {
                    this.capacity = initialCapacity;
                }
                this.values = Arrays.copyOf(this.values, capacity / 2);
            }
            return this;
        }
    }
    
}
