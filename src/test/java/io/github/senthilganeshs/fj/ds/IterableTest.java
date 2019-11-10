package io.github.senthilganeshs.fj.ds;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IterableTest {

    
    @Test
    public void testMap() throws Exception {
        List.of(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));
        Maybe.some(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));
        List.of(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));        
    }
    
    @Test
    public void testFlatMap() throws Exception {
        List.of(10).flatMap(i -> Maybe.some(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
        Maybe.some(10).flatMap(i -> List.of(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
        List.of(10).flatMap(i -> Maybe.some(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
    }
    
    @Test
    public void testApply() throws Exception {
        Assert.assertEquals(
            (int)List.of(1,2,3)
            .apply(
                Maybe.some((Integer i) -> i - 1))
            .foldLeft(0, (r, i) -> r + (Integer) i), 
            2);
        
        Assert.assertEquals(
            (int)Maybe.some(3)
            .apply(
                List.of(i -> i - 1, i -> i - 2))
            .foldLeft(0, (r, i) -> r + (Integer) i), //sum to be 1 + 2 = 3
            3);
        
        Assert.assertEquals(
            (int) List.of(1,2,3)
            .apply(
                Maybe.some((Integer i) -> i - 1))
            .foldLeft(0, (r, s) -> r + (Integer) s),
            2);
    }
    
    @Test
    public void testFilter() throws Exception {
        Assert.assertEquals(
            List.of(10,20,30).filter(i -> i == 10).toString(), List.of(10).toString());
        
        Assert.assertEquals(
            List.of(10,20,30).filter(i -> i == 10).toString(), List.of(10).toString());
      
        Assert.assertEquals(
            Maybe.some(10)
            .filter(i -> i == 20).toString(),
            Maybe.nothing().toString());
    }
    
    @Test
    public void testTraverse() throws Exception {
        Assert.assertEquals(
            (int) List.of(1,1)
            .traverse(i -> List.of('a', 'b', 'c'))
            .flatMap(id -> id)
            .foldLeft(0, (r, c) -> r + 1),
            (int) Math.pow(3, 2) * 2);
        
        Assert.assertEquals(
            (int) List.of(1,1)
            .traverse(i -> List.of('a', 'b', 'c'))
            .flatMap(id -> id)
            .foldLeft(0, (r, c) -> r + 1),
            (int) Math.pow(3, 2) * 2);     
    }
}
