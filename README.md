# Just 语言

> 一个现代化的、编译到 C 的面向对象编程语言

[![Language](https://img.shields.io/badge/language-Just-blue.svg)](https://github.com/just-lang)
[![Target](https://img.shields.io/badge/target-C99-green.svg)](https://en.wikipedia.org/wiki/C99)
[![Status](https://img.shields.io/badge/status-beta-yellow.svg)](https://github.com/just-lang)

## ✨ 特性

- 🚀 **高性能**: 编译到 C 代码，接近原生性能
- 🎯 **类型安全**: 编译时类型检查，减少运行时错误
- 📦 **面向对象**: 支持类、继承、多态
- 🔧 **零运行时开销**: 直接编译到机器码，无虚拟机
- 📝 **现代语法**: 简洁、易学、易用
- 🛠️ **C 互操作**: 无缝调用 C 库

## 🎯 快速开始

### 安装

```bash
# 克隆仓库
git clone https://github.com/just-lang/just-compiler.git
cd just-compiler

# 编译编译器（需要 Java 11+）
cd compiler
javac -d bin -encoding UTF-8 src/just/*.java src/just/**/*.java
```

### Hello World

创建 `hello.just`:

```just
class Main {
    void main() {
        println("Hello, Just!");
    }
}
```

编译和运行:

```bash
# 编译 Just 代码到 C
java -cp compiler/bin just.JustCompiler hello.just

# 使用 GCC 编译生成的 C 代码
gcc -o hello hello.c runtime/runtime.c

# 运行
./hello
```

## 📚 更多信息

- 📖 [完整文档](COMPLETION_REPORT.md)
- 🚀 [示例代码](examples/)
- 📊 [性能对比](COMPLETION_REPORT.md#性能分析)

---

**Just 语言 - 让编程更简单、更快速！** 🚀
