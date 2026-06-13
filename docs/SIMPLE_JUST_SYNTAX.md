# Simple Just Syntax / Simple Just 语法

English and Chinese are kept together in this document so the syntax reference stays consistent.

本文档采用中英双语并列说明，避免语法说明分散成两份后不同步。

## Positioning / 定位

Simple Just is a small, indentation-based syntax for Just. It is designed to feel light like a script while still compiling to C with static structures.

Simple Just 是 Just 的极简缩进式语法。它希望像脚本一样轻量，但仍然保留静态结构，并最终编译到 C。

## main Entry / main 入口

Every simple program starts with `main:`.

每个极简语法程序都从 `main:` 开始。

```just
main:
    print("hello")
```

## Indentation Blocks / 缩进代码块

Use `:` to open a block. Use 4 spaces for each nested level. Braces and semicolons are not required.

使用 `:` 开启代码块。每层缩进使用 4 个空格。不需要 `{}` 和 `;`。

```just
if age >= 18:
    print("adult")
else:
    print("child")
```

## Variables / 变量

You can declare variables explicitly.

可以显式声明变量。

```just
age int = 18
name str = "Tom"
Dog d = Dog("Mini", 1)
```

You can also let the compiler infer local variables on first assignment.

也可以在首次赋值时让编译器推导局部变量类型。

```just
next = age + 1
```

## Explicit Types / 显式类型

Top-level capitalized names define data types.

顶层大写开头的名字表示类型定义。

```just
Dog:
    name str
    age int
```

Fields use `name type`. Supported field types today are `int`, `str`, `bool`, `float`, and simple user types.

字段格式是 `字段名 类型`。当前支持 `int`、`str`、`bool`、`float` 和简单用户类型。

## Implicit Object Inference / 隐式对象推导

You can start from usage.

也可以先写对象如何使用。

```just
main:
    Dog d
    d.name = "Mini"
    d.age = 1
```

If `Dog` was not explicitly defined, fields are inferred from assignments. If `Dog` was explicitly defined, assigning an undeclared field is an error.

如果 `Dog` 没有显式定义，字段会从赋值中推导。如果 `Dog` 已经显式定义，给未声明字段赋值会报错。

## Methods / 方法

Methods live inside a type. The first generated C argument is `self`.

方法定义在类型内部。生成 C 时第一个参数是 `self`。

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

方法内部可以写 `age`，也可以写 `this.age`。参数和局部变量优先于字段。

Implicit method blocks are also supported.

也支持隐式对象方法块。

```just
main:
    Dog d
    d.name = "Mini"

    d.say(prefix str):
        print("${prefix}, ${name}")
```

## Functions / 函数

Top-level functions use `fn`.

顶层函数使用 `fn`。

```just
fn adult(age int) bool:
    age >= 18
```

The final expression in a non-void function is returned automatically.

非 `void` 函数的最后一个表达式会自动作为返回值。

## if / else

```just
if age >= 18:
    print("adult")
else:
    print("child")
```

## while / while 循环

```just
i = 0
while i < dogs.len:
    dogs[i].say("loop")
    i = i + 1
```

## for / for 遍历

Fixed arrays can be traversed with `for item in array:`.

固定长度数组可以用 `for item in array:` 遍历。

```just
for dog in dogs:
    dog.say("hello")
```

## Arrays / 数组

Arrays are fixed length in the first implementation.

当前数组是固定长度数组。

```just
dogs Dog[] = [
    Dog("Jon", 12)
    ("Bob", 13)
]
```

Tuple shorthand uses the array element type, so `("Bob", 13)` becomes `Dog("Bob", 13)`.

元组简写会使用数组元素类型，所以 `("Bob", 13)` 等价于 `Dog("Bob", 13)`。

Use `.len` for the generated array length.

使用 `.len` 访问数组长度。

```just
print(dogs.len)
```

Use indexing to access items.

使用索引访问数组元素。

```just
dogs[0].say("first")
dogs[i].age = dogs[i].age + 1
```

## Constructors / 构造表达式

Constructors use field order.

构造表达式按字段定义顺序传参。

```just
Dog d = Dog("Mini", 1)
```

The number and basic types of arguments must match the fields.

参数数量和基础类型需要与字段匹配。

## Expressions / 表达式

Supported operators:

当前支持的运算符：

```text
()
* / %
+ -
> >= < <=
== !=
&&
||
```

## String Interpolation / 字符串插值

Use `${...}` inside strings.

字符串中可以使用 `${...}` 插值。

```just
print("I am ${name}, ${age} years old")
msg = "adult? ${adult(age)}"
print(msg)
```

Supported interpolation expressions include variables, fields, array length, array element fields, method calls, and function calls.

插值表达式支持变量、字段、数组长度、数组元素字段、方法调用和函数调用。

## Comments / 注释

Single-line comments start with `//`.

单行注释使用 `//`。

```just
// ignored
print("http://example.com") // comment
```

`//` inside a string is not treated as a comment.

字符串里的 `//` 不会被当作注释。

## Current Limits / 当前限制

Simple Just is still a minimal compiler path.

Simple Just 仍然是一个最小编译路径。

- No dynamic arrays or append.
- 暂无动态数组或 append。
- No method/function overloads.
- 暂无方法/函数重载。
- No full block scoped symbol table.
- 暂无完整块级作用域符号表。
- No inheritance or interfaces.
- 暂无继承和接口。
- No arrays as function return values.
- 暂不支持数组作为函数返回值。
- No multiline comments.
- 暂无多行注释。
- No advanced string ownership model.
- 暂无正式字符串所有权模型。

## Complete Example / 完整示例

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
