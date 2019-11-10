![build](https://travis-ci.org/senthilganeshs/functional-java.svg?branch=master)
# functional-java

Functional Java is an initiative to bring functional APIs to JAVA without compromizing on object oriented programming principes.

### Features
This is just a start and came out of preparing content for this [blog](https://senthilganesh.hashnode.dev/functional-data-structures-in-java-ck2o2v8ep003lkjs1by0qpsm5)

The functional API's are influenced from Haskell which is a pure functional language.

Following data-structures are supported as of now.
1. List
2. Maybe

### Examples

We can transform an existing java.util.List<T> into an Iterable<T> so that we can use the functional APIs.

```java
List.of(1,2,3,4,5).take(2);
> [1, 2]

List.of(1,2,3,4,5).drop(2);
> [3, 4, 5]

List.of(1,2,3,4,5).reverse();
> [5, 4, 3, 2, 1]

List.of("+91","123","456","7890").intersperse("-").foldLeft("", this::concat);
> +91-123-456-7890

List.of(1,2,3,4,5,6,7,8).filter(i -> i % 2 == 0);
> [2,4,6,8]

List.of(1,2,3).map (i -> i * 2);
> [2, 4, 6]

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
```
