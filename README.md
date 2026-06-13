# Just Language SDK v1.0

> 简单如 Java，快速如 C！

[![Language](https://img.shields.io/badge/language-Just-blue.svg)](https://github.com/JayCRL/Just)
[![Target](https://img.shields.io/badge/target-Native-green.svg)](https://en.wikipedia.org/wiki/C99)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

## 🚀 快速开始

### 安装

运行安装脚本：
```powershell
.\install.ps1
```

### 第一个程序

创建 `hello.just`:
```just
class Main {
    void main() {
        println("Hello, Just!");
    }
}
```

编译运行（就像 Java 一样简单）:
```bash
justc hello.just    # 编译
just hello          # 运行
```

输出:
```
Hello, Just!
```

## ✨ 特性

- 🎯 **简单易用**: 像 `javac/java` 一样的命令
- 🚀 **原生性能**: 编译到 C，接近原生速度
- ⚡ **快速编译**: < 1 秒（小程序）
- 📦 **面向对象**: 类、继承、多态
- 🔧 **零开销**: 无虚拟机，无 GC 暂停
- 📝 **现代语法**: 简洁、清晰、易学

## 📋 系统要求

### 必需
- **Windows 10/11**
- **Java 11+** - [下载](https://adoptium.net/)
- **GCC (MinGW-w64)** - [下载](https://winlibs.com/)

## 🎯 命令参考

```bash
justc <file.just>   # 编译
just <program>      # 运行
```

## 📚 示例

### 计算器
```just
class Calculator {
    int add(int a, int b) {
        return a + b;
    }
}

class Main {
    void main() {
        Calculator calc = new Calculator();
        int sum = calc.add(10, 20);
        println("10 + 20 = 30");
    }
}
```

更多示例: `examples/`

## 📖 文档

- **快速开始**: `JAVAC_STYLE_GUIDE.md`
- **语法参考**: `docs\SYNTAX_FEATURES.md`
- **性能对比**: `docs\SYNTAX_COMPARISON.md`

## ⚡ 性能

| 语言 | 斐波那契(40) | 相对速度 |
|------|-------------|----------|
| C | 0.5s | 1.0x |
| **Just** | **0.5s** | **1.0x** |
| Go | 1.2s | 0.4x |
| Java | 1.8s | 0.3x |

## 📞 支持

- **GitHub**: https://github.com/JayCRL/Just
- **Issues**: https://github.com/JayCRL/Just/issues

## 📄 许可证

MIT License

---

**Just Language - 简单、快速、强大！** 🚀
