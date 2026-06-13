# Contributing / 开发贡献指南

English | [中文](#中文)

Just 0.2 is an experimental language project. Contributions should keep the project small, testable, and honest about what is implemented.

## Development Setup

Prerequisites:

- Windows 10/11 is the main tested environment.
- Java 11+ for running the current compiler.
- GCC or MinGW-w64 for compiling generated C.

Quick verification:

```powershell
cd C:\Users\25566\just-lang\examples
..\bin\justc.bat simple_final.just
.\simple_final.exe
```

## Compiler Layout

- `compiler/src/just/JustCompiler.java` is the compiler entry point.
- `compiler/src/just/simple/SimpleJustCompiler.java` contains the Just 0.2 minimal indentation syntax path.
- The older Java-like compiler path still exists under `lexer`, `parser`, `semantic`, and `codegen`.
- `runtime/` contains the C runtime used by generated programs.
- `examples/simple_*.just` are the current smoke tests for Simple Just.

## Building During Development

The older parser sources currently include unfinished pieces, so avoid full `javac` unless you are intentionally fixing that path. For Simple Just work, compile only the necessary classes:

```powershell
& "C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot\bin\javac.exe" `
  -encoding UTF-8 `
  -cp "C:\Users\25566\just-lang\compiler\bin" `
  -d "C:\Users\25566\just-lang\compiler\bin" `
  "C:\Users\25566\just-lang\compiler\src\just\simple\SimpleJustCompiler.java" `
  "C:\Users\25566\just-lang\compiler\src\just\JustCompiler.java"
```

## Regression Checks

Run these after changing `SimpleJustCompiler`:

```powershell
cd C:\Users\25566\just-lang\examples

..\bin\justc.bat simple_minimal.just
.\simple_minimal.exe

..\bin\justc.bat simple_stage2.just
.\simple_stage2.exe

..\bin\justc.bat simple_stage3.just
.\simple_stage3.exe

..\bin\justc.bat simple_stage4.just
.\simple_stage4.exe

..\bin\justc.bat simple_final.just
.\simple_final.exe
```

## Contribution Guidelines

- Keep language changes focused and backed by examples.
- Do not add features only to make the README look larger.
- Update [docs/SIMPLE_JUST_SYNTAX.md](docs/SIMPLE_JUST_SYNTAX.md) when syntax changes.
- Preserve the older Java-like compiler path unless the task explicitly targets it.
- Prefer clear errors over silently accepting ambiguous syntax.
- Keep generated C readable enough to inspect.

## Pull Requests

1. Fork the repository.
2. Create a focused branch.
3. Make the smallest coherent change.
4. Add or update an example when behavior changes.
5. Run the regression checks above.
6. Open a pull request with the behavior change and test output.

---

# 中文

Just 0.2 是一个实验性语言项目。贡献代码时应保持项目克制、可测试，并且如实描述已经实现的能力。

## 开发环境

前置要求：

- 当前主要测试环境是 Windows 10/11。
- 需要 Java 11+ 运行当前编译器。
- 需要 GCC 或 MinGW-w64 编译生成的 C。

快速验证：

```powershell
cd C:\Users\25566\just-lang\examples
..\bin\justc.bat simple_final.just
.\simple_final.exe
```

## 编译器结构

- `compiler/src/just/JustCompiler.java` 是编译器入口。
- `compiler/src/just/simple/SimpleJustCompiler.java` 是 Just 0.2 极简缩进语法路径。
- 旧的 Java 风格编译路径仍在 `lexer`、`parser`、`semantic`、`codegen` 下。
- `runtime/` 是生成 C 程序使用的运行时。
- `examples/simple_*.just` 是当前 Simple Just 的冒烟测试。

## 开发时编译

旧 parser 源码里还有未完成部分，所以不要随意执行全量 `javac`。如果只修改 Simple Just，编译必要 class 即可：

```powershell
& "C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot\bin\javac.exe" `
  -encoding UTF-8 `
  -cp "C:\Users\25566\just-lang\compiler\bin" `
  -d "C:\Users\25566\just-lang\compiler\bin" `
  "C:\Users\25566\just-lang\compiler\src\just\simple\SimpleJustCompiler.java" `
  "C:\Users\25566\just-lang\compiler\src\just\JustCompiler.java"
```

## 回归检查

修改 `SimpleJustCompiler` 后运行：

```powershell
cd C:\Users\25566\just-lang\examples

..\bin\justc.bat simple_minimal.just
.\simple_minimal.exe

..\bin\justc.bat simple_stage2.just
.\simple_stage2.exe

..\bin\justc.bat simple_stage3.just
.\simple_stage3.exe

..\bin\justc.bat simple_stage4.just
.\simple_stage4.exe

..\bin\justc.bat simple_final.just
.\simple_final.exe
```

## 贡献原则

- 语言改动要聚焦，并配套示例。
- 不要为了让 README 显得更强而堆功能。
- 语法变化需要同步更新 [docs/SIMPLE_JUST_SYNTAX.md](docs/SIMPLE_JUST_SYNTAX.md)。
- 除非任务明确要求，否则不要破坏旧 Java 风格编译路径。
- 遇到模糊语法时，优先给清楚错误，而不是静默接受。
- 生成的 C 应该尽量保持可读，便于检查。

## Pull Request

1. Fork 仓库。
2. 创建聚焦的分支。
3. 做一个最小但完整的改动。
4. 行为变化时增加或更新示例。
5. 运行上面的回归检查。
6. PR 中说明行为变化和测试输出。
