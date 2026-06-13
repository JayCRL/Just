# Just 语言 - 快速开始（javac 风格）

## 🚀 和 Java 一样简单！

### 安装

1. 添加 Just 到 PATH：
```powershell
$env:PATH = "C:\Users\25566\just-lang\bin;$env:PATH"
```

2. 永久添加（可选）：
   - 右键"此电脑" → 属性 → 高级系统设置 → 环境变量
   - 编辑 PATH，添加：`C:\Users\25566\just-lang\bin`

---

## 📝 使用方法

### 步骤 1: 编写代码

创建 `hello.just`:
```just
class Main {
    void main() {
        println("Hello, Just!");
    }
}
```

### 步骤 2: 编译

```bash
justc hello.just
```

输出：
```
Compiling hello.just...
Success: hello.exe
```

### 步骤 3: 运行

```bash
just hello
```

输出：
```
Hello, Just!
```

---

## 🎯 完整示例

```bash
# 创建程序
echo class Main { void main() { println("Works!"); } } > test.just

# 编译
justc test.just

# 运行
just test
```

---

## 📊 和其他语言对比

### Java:
```bash
javac Hello.java
java Hello
```

### C:
```bash
gcc hello.c -o hello
./hello
```

### Go:
```bash
go build hello.go
./hello
```

### Just:
```bash
justc hello.just    # 编译
just hello          # 运行
```

**Just 和 Java 一样简单，但性能接近 C！**

---

## ⚡ 特点

- **简单**: 就像 `javac/java`
- **快速**: 编译 < 1 秒
- **高效**: 原生性能，无虚拟机
- **纯净**: 自动清理中间文件

---

## 🎓 更多示例

### 示例 1: 计算器

```just
class Calculator {
    int add(int a, int b) {
        return a + b;
    }
}

class Main {
    void main() {
        Calculator calc = new Calculator();
        int result = calc.add(10, 20);
        println("10 + 20 = 30");
    }
}
```

编译运行：
```bash
justc calc.just
just calc
```

### 示例 2: 面向对象

```just
class Point {
    int x;
    int y;
    
    Point(int px, int py) {
        x = px;
        y = py;
    }
    
    void print() {
        println("Point");
    }
}

class Main {
    void main() {
        Point p = new Point(10, 20);
        p.print();
    }
}
```

编译运行：
```bash
justc point.just
just point
```

---

## 🛠️ 命令参考

### justc - 编译器
```bash
justc <file.just>    # 编译 Just 源文件
```

**自动完成**：
- Just 代码 → C 代码
- C 代码 → 可执行文件
- 清理中间文件

### just - 运行器
```bash
just <program>       # 运行编译好的程序
```

---

## 📦 目录结构

```
just-lang/
├── bin/
│   ├── justc.bat    # 编译器命令
│   └── just.bat     # 运行器命令
├── compiler/        # Just 编译器
├── runtime/         # 运行时库
└── examples/        # 示例程序
```

---

## 🎯 工作流程

```
hello.just
    ↓ [justc]
hello.exe
    ↓ [just]
运行！
```

**两个命令，搞定一切！**

---

## 💡 提示

### 编译多个文件
```bash
justc file1.just
justc file2.just
justc file3.just
```

### 查看中间文件
```bash
justc hello.just --keep-c    # (未实现，但可以手动保留)
```

### 直接运行
```bash
justc hello.just && just hello
```

---

## 🏆 Just 的优势

| 特性 | Java | Just |
|------|------|------|
| **编译** | `javac` | `justc` ✅ |
| **运行** | `java` | `just` ✅ |
| **性能** | JVM | 原生 ✅ |
| **启动** | 慢 | 即时 ✅ |
| **内存** | 大 | 小 ✅ |
| **语法** | 复杂 | 简洁 ✅ |

---

**Just 语言 - 简单如 Java，快速如 C！** 🚀

## 📚 下一步

- 查看 `examples/` 目录的更多示例
- 阅读 `SYNTAX_FEATURES.md` 了解完整语法
- 阅读 `SYNTAX_COMPARISON.md` 对比其他语言

---

**开始使用 Just 吧！** ✨
