![build](https://travis-ci.org/senthilganeshs/functional-java.svg?branch=master)
# functional-java

Functional Java is an initiative to bring functional APIs to JAVA without compromizing on object oriented programming principes.

### Features
This is just a start and came out of preparing content for this [blog](https://senthilganesh.hashnode.dev/functional-data-structures-in-java-ck2o2v8ep003lkjs1by0qpsm5)

The functional API's are influenced from Haskell which is a pure functional language.

Following data-structures are supported as of now.
1. List
2. Maybe
3. Tuple
4. Either
5. BinaryTree (Set)

### Examples

```java
List.of(1,2,3,4,5).take(2);
> [1, 2]

List.of(1,2,3,4,5).drop(2);
> [3, 4, 5]

List.of(1,2,3,4,5).reverse();
> [5, 4, 3, 2, 1]

List.of("+91","123","456","7890").intersperse("-").foldl("", this::concat);
> +91-123-456-7890

List.of(1,2,3,4,5,6,7,8).filter(i -> i % 2 == 0);
> [2,4,6,8]

List.of(1,2,3).map (i -> i * 2);
> [2, 4, 6]

Maybe.some(5).concat(List.of(1,2,3,4))
> [1,2,3,4,5]

List.of(1,4,9).concat(BinaryTree.of(3))
> { Label : 3, left = { Label : 1, left = [], right = [] }, right = { Label : 4, left = [], right = { Label : 9, left = [], right = [] } } }

List.of(1,2,3).flatMap(i -> (i < 3) ? Maybe.some(i) : Maybe.nothing());
> Nothing

List.of(1,2,3).flatMap(i -> (i <=3) ? Maybe.some(i) : Maybe.nothing());
> [1,2,3]

List.of(1,2,3).apply(List.of(i -> i +1, i -> i + 2));
> [2, 3, 4, 3, 4, 5]

List.of(1,2,3).apply(Maybe.some(i -> i + 1));
> Some(4)

List.of(1,2,3).traverse(i -> Maybe.some(i + 1));
> Some ([2,3,4])

List.of(1,2,3).traverse (i -> List.of('a', 'b'));
> [[a,a,a],[b,a,a],[a,b,a],[b,b,a],[a,a,b],[b,a,b],[a,b,b],[b,b,b]]

Iterable.sequence(List.of(1,2,3).traverse(i -> Maybe.some(i + 1)));
> [Some (2),Some (3),Some (4)]

Iterable.sequence(Iterable.sequence(List.of(1,2,3).traverse(i -> Maybe.some(i + 1))));
> Some ([2, 3, 4])

Iterable.lefts(List.of(Either.right(1), Either.left(2), Either.right(3)))
> [2]

Iterable.rights(List.of(Either.right(1), Either.left(2), Either.right(3)))
> [1, 3]

Iterable.partition(List.of(Either.right(10), Either.left(20), Either.right(30)))
> ([20], [10,30])

BinaryTree.of(3,1,2,5,4).contains(2)
> true

List.of(3,1,2,5,4).sort()
> [1, 2, 3, 4, 5]

Tuple.of ('a', 1).swap()
> (1, a)

List.of(1,2).zipper()
> ([1,2], )

Iterable2.goForward(List.of(1,2).zipper())
> ([1], [2])

Iterable2.goForward(Iterable2.goForward(List.of(1,2).zipper()))
> (, [2,1])

Iterable2.goBack(Iterable2.goForward(List.of(1,2).zipper()))
> ([1,2], )
```
