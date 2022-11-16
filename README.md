![build](https://travis-ci.org/senthilganeshs/functional-java.svg?branch=master)
# functional-java

Functional Java is an initiative to bring functional APIs to JAVA without compromizing on object oriented programming principes.

Maven repository
https://mvnrepository.com/artifact/io.github.senthilganeshs/functional-java/1.0.0
```xml
<!-- https://mvnrepository.com/artifact/io.github.senthilganeshs/functional-java -->
<dependency>
    <groupId>io.github.senthilganeshs</groupId>
    <artifactId>functional-java</artifactId>
    <version>1.0.0</version>
</dependency>
```


### Features
This is just a start and came out of preparing content for this [blog](https://senthilganesh.hashnode.dev/functional-data-structures-in-java-ck2o2v8ep003lkjs1by0qpsm5)

The functional API's are influenced from Haskell which is a pure functional language.

Following data-structures are supported as of now.
1. List
2. Maybe
3. Either
4. Set

### Examples

```java
List.of(1,2,3,4,5).take(2);
> [1, 2]

List.of(1,2,3,4,5).drop(2);
> [3, 4, 5]

List.of(1,2,3,4,5).reverse();
> [5, 4, 3, 2, 1]

List.of("+91","123","456","7890").intersperse("-").foldl("", (a, b) -> a + b);
> +91-123-456-7890

List.of(1,2,3,4,5,6,7,8).filter(i -> i % 2 == 0);
> [2,4,6,8]

List.of(1,2,3).map (i -> i * 2);
> [2, 4, 6]

List.of(1,2,3,4).concat(Maybe.some(5));
> [1,2,3,4,5]

Set.of(3).concat(List.of(1,4,9));
> { Label : 3, left = { Label : 1, left = [], right = [] }, right = { Label : 4, left = [], right = { Label : 9, left = [], right = [] } } }

List.of(1,2,3).flatMap(i -> (i < 3) ? Maybe.some(i) : Maybe.nothing());
> [1, 2]

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

Collection.sequence(List.of(1,2,3).traverse(i -> Maybe.some(i + 1)));
> [Some (2),Some (3),Some (4)]

Collection.sequence(Collection.sequence(List.of(1,2,3).traverse(i -> Maybe.some(i + 1))));
> Some ([2, 3, 4])

Either.lefts(List.of(Either.right(1), Either.left(2), Either.right(3)))
> [2]

Either.rights(List.of(Either.right(1), Either.left(2), Either.right(3)))
> [1, 3]

Set.of(3,1,2,5,4).contains(2)
> true

Set.sort(List.of(3,1,2,5,4))
> [1, 2, 3, 4, 5]

```
