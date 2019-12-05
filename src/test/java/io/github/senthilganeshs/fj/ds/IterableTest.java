package io.github.senthilganeshs.fj.ds;

import org.testng.Assert;
import org.testng.annotations.Test;


public class IterableTest {

    
    @Test
    public void testMap() throws Exception {
        List.of(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));
        Maybe.some(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));
        Either.right(10).map(i -> i + 10).forEach(i -> Assert.assertTrue(i == 20));
        BinaryTree.of(3,1,2,4,5).map(i -> i + 10).equals(List.of(11,12,13,14,15));        
    }
    
    @Test
    public void testFlatMap() throws Exception {
        List.of(10).flatMap(i -> Maybe.some(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
        Maybe.some(10).flatMap(i -> List.of(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
        Either.right(10).flatMap(i -> Maybe.some(i + 10)).forEach(i -> Assert.assertTrue(i == 20));
        BinaryTree.of(3,1,2,4,5).flatMap(i -> Maybe.some(i + 10)).equals(List.of(11,12,13,14,15));
    }
    
    @Test
    public void testApply() throws Exception {
        Assert.assertEquals(
            (int)List.of(1,2,3)
            .apply(
                Maybe.some((Integer i) -> i - 1))
            .foldl(0, (r, i) -> r + (Integer) i), 
            2);
        
        Assert.assertEquals(
            (int)Maybe.some(3)
            .apply(
                List.of(i -> i - 1, i -> i - 2))
            .foldl(0, (r, i) -> r + (Integer) i), //sum to be 1 + 2 = 3
            3);
        
        Assert.assertEquals(
            (int)Either.right(3)
            .apply(
                List.of(i -> i - 1, i -> i - 2))
            .foldl(0, (r, i) -> r + (Integer) i), //sum to be 1 + 2 = 3
            3);
        
       Assert.assertEquals(
            BinaryTree.of(3,1,2,5,4).apply(Maybe.some(i -> i - 1)), 
            Maybe.some(4));
    }
    
    @Test
    public void testFilter() throws Exception {
        Assert.assertEquals(
            List.of(10,20,30).filter(i -> i == 10).toString(), List.of(10).toString());
        
        Assert.assertEquals(
            Maybe.some(10)
            .filter(i -> i == 20).toString(),
            Maybe.nothing().toString());
        
        Assert.assertEquals(
            Either.right(10)
            .filter(i -> i == 20).toString(),
            Either.left(10).toString());
        
        Assert.assertEquals(
            BinaryTree.of(3,1,2,5,4)
            .filter(i -> i == 2),
            BinaryTree.of(2));
        
    }
    
    @Test
    public void testTraverse() throws Exception {
        Assert.assertEquals(
            (int) List.of(1,1)
            .traverse(i -> List.of('a', 'b', 'c'))
            .flatMap(id -> id)
            .foldl(0, (r, c) -> r + 1),
            (int) Math.pow(3, 2) * 2);
        
        Assert.assertEquals(
            (int) Maybe.some(1)
            .traverse(i -> List.of('a', 'b', 'c'))
            .flatMap(id -> id)
            .foldl(0, (r, c) -> r + 1),
            3);        
        
        Assert.assertEquals(
            (int) Either.right(1)
            .traverse(i -> List.of('a', 'b', 'c'))
            .flatMap(id -> id)
            .foldl(0, (r, c) -> r + 1),
            3);
        
        Assert.assertEquals(
            BinaryTree.of(3,1,2,5,4)
            .traverse(i -> Maybe.some(i))
            .flatMap(id -> id),
            Maybe.some(5));
    }
    
    @Test
    public void testSequence() throws Exception {
        Assert.assertEquals(
            Iterable.sequence(
                List.of(Maybe.some(1), Maybe.some(2))),
            Maybe.some(List.of(1, 2)));
        
        Assert.assertEquals(
            Iterable.sequence(
                List.of(Either.right(1), Either.left(2))),
            Either.left(2));                       
    }
    
    @Test
    public void testEither() throws Exception {
        Assert.assertEquals(
            Iterable.rights(
                List.of(Either.right(10), Either.left(20), Either.right(30))),
            List.of(10,30));
        
        Assert.assertEquals(
            Iterable.lefts(
                List.of(Either.right(10), Either.left(20), Either.right(30))),
            List.of(20));
        
        Assert.assertEquals(
            Iterable.partition(
                List.of(Either.right(10), Either.left(20), Either.right(30))),
                Tuple.of(List.of(20), List.of(10,30)));
        
        Assert.assertEquals(
            Iterable.partition(
                Maybe.some(Either.right(10))),
                Tuple.of(Maybe.nothing(), Maybe.some(10)));
        
        Assert.assertTrue(Either.right(10).either(x -> false, b -> b == 10));
        Assert.assertTrue(Either.left(10).either(x -> x == 10, b -> false));
        Assert.assertEquals((int) Either.left(10).fromLeft(11), 10);
        Assert.assertEquals((int) Either.left(10).fromRight(11), 11);
        Assert.assertEquals(Either.right(10).fromLeft(11), 11);
        Assert.assertEquals((int) Either.right(10).fromRight(11), 10);
        
    }
    
    @Test
    public void testTuple() throws Exception {
        Assert.assertEquals(Tuple.of(10, 20).swap(), Tuple.of(20, 10));
    }
    
    @Test
    public void testList() throws Exception {
        Assert.assertEquals(
            Iterable.sort(List.of(3,1,2,5,4)),
            List.of(1,2,3,4,5));
        
        Assert.assertEquals(
            List.of(3,1,2,5,4).length(), 5);
        
        Assert.assertEquals(
            List.of(1,2,3,4,5).drop(2), 
            List.of(3,4,5));
        
        Assert.assertEquals(
            List.of(1,2,3,4,5).take(2), 
            List.of(1,2));
        
        Assert.assertEquals(
            List.of(1,2,3,4,5).reverse(),
            List.of(5,4,3,2,1));
        
        Assert.assertEquals(
            List.of('a','b','c','d','e')
            .intersperse(','), 
            List.of('a',',','b',',','c',',','d',',','e'));
        
        Assert.assertEquals(
            List.intercalate(
                List.of(','),
                List.of(List.of('a','b'),List.of('c','d','e'))), 
            List.of(List.of('a','b'),List.of(','),List.of('c','d','e')));
        
        Assert.assertEquals(
            List.or(
                List.of(true, true, false)), (Boolean) true);
        
        Assert.assertEquals(
            List.and(
                List.of(true, true, false)), (Boolean) false);
    }
    
    @Test
    public void testBinarytree() throws Exception {
        Assert.assertEquals(BinaryTree.of(1,2,3).compareTo(2), 0);//left rotation.
        Assert.assertEquals(BinaryTree.of(3,1,2).compareTo(2), 0);//left-right rotation
        Assert.assertEquals(BinaryTree.of(1,3,2).compareTo(2), 0);//right-left rotation
        Assert.assertEquals(BinaryTree.of(3,2,1).compareTo(2), 0);//right rotation
        
        Assert.assertTrue(BinaryTree.of(1,2,3,4,5,6,7,8,9).contains(5));
        Assert.assertFalse(BinaryTree.of(1,2,3,4,5,6,7,8).contains(9));                
    }
    
    @Test
    public void testZipper () throws Exception {
        Assert.assertTrue(Iterable2.goForward(List.of(1,2,3,4).zipper()).equals(Tuple.of(List.of(1,2,3), List.of(4))));
        Assert.assertTrue(Iterable2.goBack(Iterable2.goForward(List.of(1,2,3,4).zipper())).equals(Tuple.of(List.of(1,2,3,4), List.of())));
        
    }
    
    @Test
    public void testZip () throws  Exception {
        System.out.println(List.zip(List.of(1,2,3), List.of('a','b','c')));
    }
}
