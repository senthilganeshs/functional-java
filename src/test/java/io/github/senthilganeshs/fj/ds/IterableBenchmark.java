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
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx12G"})
public class IterableBenchmark {

    @State(Scope.Thread)
    public static class IntValues {
        @Param({"1000000", "10000000"})
        int size;
        
        java.util.List<Integer> array;
        
        Integer find;
        
        @Setup
        public void setup() {
            array = new ArrayList<Integer>(size);
            
            for (int i = 0; i < size; i ++) {
                array.add(Double.valueOf(Math.random()).intValue());
            }
            find = array.get(new Random().nextInt(size));
        }
    }
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(IterableBenchmark.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }
    
    @Benchmark
    public void binaryTreeAsSet(final IntValues input) {
        if (!BinaryTree.of(input.array).contains(input.find)) {
            throw new RuntimeException("failed to check the value in set");
        }
    }
    
    @Benchmark
    public void treeSet (final IntValues input) {
        if (!(new TreeSet<>(input.array)).contains(input.find)) {
            throw new RuntimeException ("failed to check the value in set");
        }
    }
}
