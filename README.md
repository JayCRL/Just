# Just 0.2

Just is an experimental statically typed language with a minimal indentation-based syntax. It compiles to C and explores compile-time object structure inference: you can define data shapes explicitly, or let the compiler infer simple object fields from how values are used.

This repository is a language experiment, not a production compiler. The current focus is a small, coherent syntax that is easy to read, easy to generate, and direct to lower into C.

## Example

The main example is [examples/simple_final.just](examples/simple_final.just):

```just
// Just minimal syntax final demo

Dog:
    name str
    age int

    say(prefix str):
        print("${prefix}, I am ${name}, ${age} years old")

    grow:
        age = age + 1

    olderThan(limit int) bool:
        age > limit

fn adult(age int) bool:
    age >= 18

main:
    dogs Dog[] = [
        ("Jon", 12)
        ("Bob", 13)
        ("Alice", 20)
    ]

    print("dog count: ${dogs.len}")

    dogs[0].say("first")

    i = 0
    while i < dogs.len:
        dogs[i].say("loop")
        print(dogs[i].olderThan(18))
        dogs[i].age = dogs[i].age + 1
        i = i + 1

    Dog d = Dog("Mini", 1)
    d.grow()
    d.say("after grow")

    msg = "Mini adult? ${adult(d.age)}"
    print(msg)
```

Expected output:

```text
dog count: 3
first, I am Jon, 12 years old
loop, I am Jon, 12 years old
false
loop, I am Bob, 13 years old
false
loop, I am Alice, 20 years old
true
after grow, I am Mini, 2 years old
Mini adult? false
```

## Quick Start

From a Windows shell:

```powershell
cd examples
..\bin\justc.bat simple_final.just
.\simple_final.exe
```

The current Windows compiler script expects Java and GCC to be available, unless you are using a bundled SDK build.

## Supported Features

- `main:` entry point
- Indentation blocks using `:`
- Static primitive types: `int`, `str`, `bool`, `float`
- Explicit type definitions:
  ```just
  Dog:
      name str
      age int
  ```
- Compile-time object field inference for simple undeclared types:
  ```just
  Dog d
  d.name = "Mini"
  d.age = 1
  ```
- Constructors lowered to C struct initialization:
  ```just
  Dog d = Dog("Mini", 1)
  ```
- Fixed-length arrays:
  ```just
  dogs Dog[] = [
      ("Jon", 12)
      ("Alice", 20)
  ]
  ```
- Array indexing and `.len`
- `for item in array:` traversal
- `while` loops
- `if/else`
- Top-level `fn` functions
- Instance methods with parameters and return values
- Method bodies can access fields as `age` or `this.age`
- Local variable inference on first assignment
- Basic expressions with precedence: `* / %`, `+ -`, comparisons, `== !=`, `&&`, `||`
- String interpolation:
  ```just
  print("I am ${name}, ${age} years old")
  ```
- Single-line comments with `//`

## Current Limitations

- Just 0.2 is experimental and intentionally small.
- The new indentation syntax currently lives in `SimpleJustCompiler`, an independent compiler path.
- The older Java-like syntax path still exists, but it is not the focus of Just 0.2.
- There is no full token-based parser for the simple syntax yet; parts of the parser are still line-oriented.
- No dynamic arrays, `append`, `filter`, or `map`.
- No function or method overloads.
- No generics, inheritance, interfaces, modules, or packages.
- No complete block-scoped symbol table.
- No ownership or lifetime model for generated strings.
- Error reporting is improving, but still limited.

## Documentation

- [Simple Just Syntax](docs/SIMPLE_JUST_SYNTAX.md)
- [Syntax Features](docs/SYNTAX_FEATURES.md)
- [Syntax Comparison](docs/SYNTAX_COMPARISON.md)

Older implementation notes and completion reports are archived under `docs/archive/`.

## Roadmap

Near-term work:

- Replace the line-oriented simple parser with a real lexer/parser using `NEWLINE`, `INDENT`, and `DEDENT` tokens.
- Formalize the AST for the simple syntax instead of using ad hoc internal nodes.
- Improve diagnostics with better source spans.
- Add focused tests for parser, semantic inference, and C generation.
- Clarify the relationship between the older Java-like syntax and the new minimal syntax.
- Reduce compiler/runtime packaging friction.

Longer-term questions:

- Whether Just should continue lowering to C or move to a lower-level IR.
- How much object inference should remain in the language.
- How strings, arrays, and memory ownership should work beyond the current prototype.

## License

MIT License. See [LICENSE](LICENSE).
