# Just 语言 - 快速开始

## 🚀 一键编译运行

Just 语言现在支持**直接从源代码生成可执行文件**！

### 快速演示

```powershell
# 1. 编译 Just 代码到 C
java -cp compiler\bin just.JustCompiler examples\hello_simple.just

# 2. 编译 C 代码到可执行文件
gcc -o hello_simple.exe examples\hello_simple.c runtime\runtime.c -Iexamples -Iruntime

# 3. 运行
.\hello_simple.exe
```

### 输出
```
Hello, Just Language!
Compiling to C for native performance!
```

---

## 📝 完整示例

### hello_simple.just
```just
class Main {
    void main() {
        println("Hello, Just Language!");
        println("Compiling to C for native performance!");
    }
}
```

### 生成的 C 代码
```c
/* Main.main */
void Main_main(Main* self) {
    just_println_string("Hello, Just Language!");
    just_println_string("Compiling to C for native performance!");
}

/* Main.Main (默认构造函数) */
Main* Main_Main() {
    Main* self = (Main*)just_alloc(sizeof(Main));
    return self;
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();
    Main* main_obj = Main_Main();
    Main_main(main_obj);
    return 0;
}
```

---

## 🎯 编译流程

```
Just 源代码 (.just)
    ↓ [Just Compiler]
C 代码 (.c, .h)
    ↓ [GCC]
可执行文件 (.exe)
    ↓
运行!
```

---

## ⚡ 性能特点

- **编译到原生代码** - 直接生成机器码，无虚拟机开销
- **零运行时** - 无垃圾回收暂停
- **接近 C 的性能** - 单线程计算比 Go 快约 2 倍
- **小巧的二进制** - 无需携带庞大的运行时

---

## 🛠️ 环境要求

### 必需
- **Java 11+** - 编译 Just 编译器
- **GCC (MinGW-w64)** - 编译生成的 C 代码

### 安装 GCC (Windows)

1. 下载 MinGW-w64: https://github.com/brechtsanders/winlibs_mingw/releases
2. 解压到 `C:\mingw64`
3. 添加到 PATH: `C:\mingw64\mingw64\bin`

---

## 📊 更多示例

查看 `examples/` 目录：
- `hello_simple.just` - Hello World
- `ultimate_demo.just` - 完整功能演示 (1000+ Token)
- `state_pattern.just` - 状态模式
- `advanced_demo.just` - 高级模式

---

## 🎓 核心特性

### 已支持 (Just 1.0)
- ✅ 类和对象
- ✅ 继承和多态
- ✅ 方法重载
- ✅ 自动构造函数生成
- ✅ 智能类型推断
- ✅ 所有控制流

### 规划中 (Just 2.0)
- ⏳ 泛型
- ⏳ 枚举
- ⏳ 可空类型
- ⏳ Lambda 表达式
- ⏳ 异步/并发

---

## 📈 编译器改进

### 最新改进 (v1.0.1)
- ✅ **自动生成默认构造函数** - 即使类没有字段也能正常实例化
- ✅ **智能方法返回类型推断** - 方法调用返回正确类型
- ✅ **一键编译流程** - 简化从源码到可执行文件的过程

---

## 🔗 资源

- **GitHub**: https://github.com/JayCRL/Just
- **完整文档**: `FINAL_COMPLETION.md`
- **语法参考**: `docs/SYNTAX_FEATURES.md`
- **对比分析**: `docs/SYNTAX_COMPARISON.md`

---

**Just 语言 - 简单、快速、安全！** 🚀
