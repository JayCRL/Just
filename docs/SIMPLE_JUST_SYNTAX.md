# Simple Just Syntax

Simple Just is a small, indentation-based syntax for Just. It is designed to feel light like a script while still compiling to C with static structures.

## main Entry

Every simple program starts with `main:`.

```just
main:
    print("hello")
```

## Indentation Blocks

Use `:` to open a block. Use 4 spaces for each nested level. Braces and semicolons are not required.

```just
if age >= 18:
    print("adult")
else:
    print("child")
```

## Variables

You can declare variables explicitly:

```just
age int = 18
name str = "Tom"
Dog d = Dog("Mini", 1)
```

You can also let the compiler infer local variables on first assignment:

```just
next = age + 1
```

## Explicit Types

Top-level capitalized names define data types.

```just
Dog:
    name str
    age int
```

Fields use `name type`. Supported field types today are `int`, `str`, `bool`, `float`, and simple user types.

## Implicit Object Inference

You can still start from usage:

```just
main:
    Dog d
    d.name = "Mini"
    d.age = 1
```

If `Dog` was not explicitly defined, fields are inferred from assignments. If `Dog` was explicitly defined, assigning an undeclared field is an error.

## Methods

Methods live inside a type. The first C argument is generated as `self`.

```just
Dog:
    name str
    age int

    say(prefix str):
        print("${prefix}, I am ${name}")

    olderThan(limit int) bool:
        age > limit
```

Inside a method, fields can be written as `age` or `this.age`. Parameters and local variables take priority over fields.

Implicit method blocks are also supported:

```just
main:
    Dog d
    d.name = "Mini"

    d.say(prefix str):
        print("${prefix}, ${name}")
```

## Functions

Top-level functions use `fn`.

```just
fn adult(age int) bool:
    age >= 18
```

The final expression in a non-void function is returned automatically.

## if / else

```just
if age >= 18:
    print("adult")
else:
    print("child")
```

## while

```just
i = 0
while i < dogs.len:
    dogs[i].say("loop")
    i = i + 1
```

## for

Fixed arrays can be traversed with `for item in array:`.

```just
for dog in dogs:
    dog.say("hello")
```

## Arrays

Arrays are fixed length in the first implementation.

```just
dogs Dog[] = [
    Dog("Jon", 12)
    ("Bob", 13)
]
```

Tuple shorthand uses the array element type, so `("Bob", 13)` becomes `Dog("Bob", 13)`.

Use `.len` for the generated array length:

```just
print(dogs.len)
```

Use indexing to access items:

```just
dogs[0].say("first")
dogs[i].age = dogs[i].age + 1
```

## Constructors

Constructors use field order.

```just
Dog d = Dog("Mini", 1)
```

The number and basic types of arguments must match the fields.

## Expressions

Supported operators:

```text
()
* / %
+ -
> >= < <=
== !=
&&
||
```

## String Interpolation

Use `${...}` inside strings.

```just
print("I am ${name}, ${age} years old")
msg = "adult? ${adult(age)}"
print(msg)
```

Supported interpolation expressions include variables, fields, array length, array element fields, method calls, and function calls.

## Comments

Single-line comments start with `//`.

```just
// ignored
print("http://example.com") // comment
```

`//` inside a string is not treated as a comment.

## Current Limits

Simple Just is still a minimal compiler path:

- No dynamic arrays or append.
- No method/function overloads.
- No full block scoped symbol table.
- No inheritance or interfaces.
- No arrays as function return values.
- No multiline comments.
- No advanced string ownership model.

## Complete Example

```just
Dog:
    name str
    age int

    say(prefix str):
        print("${prefix}, I am ${name}, ${age} years old")

    grow:
        age = age + 1

main:
    dogs Dog[] = [
        ("Jon", 12)
        ("Alice", 20)
    ]

    i = 0
    while i < dogs.len:
        dogs[i].say("loop")
        dogs[i].age = dogs[i].age + 1
        i = i + 1
```
