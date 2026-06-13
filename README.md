# Just 0.2

English | [中文](#just-02-中文)

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
- Explicit type definitions
- Compile-time object field inference for simple undeclared types
- Constructors lowered to C struct initialization
- Fixed-length arrays, indexing, and `.len`
- `for item in array:` traversal
- `while` loops
- `if/else`
- Top-level `fn` functions
- Instance methods with parameters and return values
- Method bodies can access fields as `age` or `this.age`
- Local variable inference on first assignment
- Basic expressions with precedence
- String interpolation
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
- [Contributing / Development Guide](CONTRIBUTING.md)
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

---

# Just 0.2 中文

[English](#just-02) | 中文

Just 是一个实验性的静态类型语言，采用极简缩进式语法。它目前编译到 C，并探索“编译期对象结构推导”：你既可以显式定义数据结构，也可以先写对象如何使用，再让编译器推导简单字段。

这个仓库是语言实验项目，不是生产级编译器。当前目标是做出一套小而完整、容易阅读、容易生成、并且能直接降级到 C 的语法。

## 示例

主示例是 [examples/simple_final.just](examples/simple_final.just)，内容与上方英文部分一致。

## 快速开始

在 Windows shell 中运行：

```powershell
cd examples
..\bin\justc.bat simple_final.just
.\simple_final.exe
```

当前 Windows 编译脚本需要系统可用的 Java 和 GCC；如果使用封装版 SDK，则可以由 SDK 内置工具链提供。

## 已支持特性

- `main:` 程序入口
- 使用 `:` 和缩进表示代码块
- 静态基础类型：`int`、`str`、`bool`、`float`
- 显式类型定义
- 简单未声明类型的编译期字段推导
- 构造表达式会降级为 C struct 初始化
- 固定长度数组、数组索引和 `.len`
- `for item in array:` 遍历
- `while` 循环
- `if/else`
- 顶层 `fn` 函数
- 带参数和返回值的实例方法
- 方法体中可以用 `age` 或 `this.age` 访问字段
- 局部变量首次赋值时自动推导
- 带优先级的基础表达式
- 字符串插值
- `//` 单行注释

## 当前限制

- Just 0.2 仍是实验性版本，刻意保持较小范围。
- 新的缩进语法目前位于独立的 `SimpleJustCompiler` 编译路径中。
- 旧的 Java 风格语法路径仍然存在，但不是 Just 0.2 的重点。
- 极简语法还没有完整 token parser，部分解析仍然基于行。
- 暂无动态数组、`append`、`filter`、`map`。
- 暂无函数/方法重载。
- 暂无泛型、继承、接口、模块、包系统。
- 暂无完整块级作用域符号表。
- 生成字符串还没有正式所有权/生命周期模型。
- 错误提示正在改善，但仍然有限。

## 文档

- [Simple Just 语法 / Simple Just Syntax](docs/SIMPLE_JUST_SYNTAX.md)
- [贡献与开发指南 / Contributing](CONTRIBUTING.md)
- [语法特性](docs/SYNTAX_FEATURES.md)
- [语法对比](docs/SYNTAX_COMPARISON.md)

旧的实现笔记和完成报告已归档到 `docs/archive/`。

## 路线图

近期工作：

- 用真正的 lexer/parser 替换当前基于行的 simple parser，引入 `NEWLINE`、`INDENT`、`DEDENT` token。
- 正式化极简语法 AST，减少临时内部节点。
- 改进错误提示和源码位置标注。
- 为 parser、语义推导、C 代码生成增加集中测试。
- 明确旧 Java 风格语法和新极简语法之间的关系。
- 降低编译器和运行时打包成本。

长期问题：

- Just 是否继续降级到 C，还是迁移到更底层的 IR。
- 对象结构推导应该保留到什么程度。
- 字符串、数组、内存所有权应该如何设计。
