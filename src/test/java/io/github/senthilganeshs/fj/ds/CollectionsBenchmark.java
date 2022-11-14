package io.github.senthilganeshs.fj.ds;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(jvmArgs = {"-Xms2G", "-Xmx2G"})
public class CollectionsBenchmark {

    @State(Scope.Thread)
    public static class IntValues {
        @Param({"100000"})
        int size;
        
        java.util.List<Integer> array;
        
        Integer find;
        
        @Setup
        public void setup() {
            array = new ArrayList<Integer>(size);
            Random rnd = new Random(0);
            for (int i = 0; i < size; i ++) {
                array.add(rnd.nextInt(size));
            }
            find = array.get(rnd.nextInt(size));
        }
    }
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(CollectionsBenchmark.class.getSimpleName())
            .build();
        new Runner(opt).run();
    }
    
    @Benchmark
    public void iterableSet(final IntValues input) {
        if (!Set.of(input.array).contains(input.find)) {
            throw new RuntimeException("failed to check the value in set");
        }
    }
    
    @Benchmark
    public void javaUtilTreeSet (final IntValues input) {
        if (!(new TreeSet<>(input.array)).contains(input.find)) {
            throw new RuntimeException ("failed to check the value in set");
        }
    }
    
    @Benchmark
    public void iterableSetFind(final IntValues input) {
        Set<Integer> set = Set.of(input.array);
        Random rnd = new Random(0);
        for (int i = 0; i < 1000; i ++) {
            set.contains(rnd.nextInt(input.size));
        }
    }
    
    @Benchmark
    public void javaUtilTreeSetFind (final IntValues input) {
        java.util.Set<Integer> set = new TreeSet<>(input.array);
        Random rnd = new Random(0);
        for (int i = 0; i < 1000; i ++) {
            set.contains(rnd.nextInt(input.size));
        }
    }
}