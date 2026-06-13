# Just 语言安装指南

本指南帮助你在 Windows 上安装 Just 语言编译器所需的依赖。

## 前置要求

Just 语言编译器需要以下工具：

1. **Java JDK 8+** - 用于运行 Just 编译器
2. **GCC 编译器** - 用于编译生成的 C 代码

---

## 1. 安装 Java JDK

### 方法 A：Oracle JDK（推荐）

1. 访问 Oracle JDK 下载页面：
   https://www.oracle.com/java/technologies/downloads/

2. 下载适合 Windows 的 JDK（推荐 JDK 17 或更高版本）

3. 运行安装程序，按默认选项安装

4. 配置环境变量：
   - 打开"系统属性" → "高级" → "环境变量"
   - 在"系统变量"中新建：
     - 变量名：`JAVA_HOME`
     - 变量值：`C:\Program Files\Java\jdk-17`（根据实际安装路径调整）
   - 编辑"系统变量"中的 `Path`，添加：
     - `%JAVA_HOME%\bin`

5. 验证安装：
   ```bash
   java -version
   javac -version
   ```

### 方法 B：OpenJDK

1. 访问 Adoptium（前身 AdoptOpenJDK）：
   https://adoptium.net/

2. 下载适合 Windows 的 JDK，安装时选择"添加到 PATH"

3. 验证安装（同上）

---

## 2. 安装 GCC 编译器

### 方法 A：MinGW-w64（推荐）

1. 访问 MSYS2 官网：
   https://www.msys2.org/

2. 下载并安装 MSYS2

3. 打开 MSYS2 终端，运行：
   ```bash
   pacman -S mingw-w64-x86_64-gcc
   ```

4. 将 GCC 添加到系统 PATH：
   - 打开"系统属性" → "高级" → "环境变量"
   - 编辑"系统变量"中的 `Path`，添加：
     - `C:\msys64\mingw64\bin`

5. 验证安装：
   ```bash
   gcc --version
   ```

### 方法 B：TDM-GCC

1. 访问 TDM-GCC 官网：
   https://jmeubank.github.io/tdm-gcc/

2. 下载并安装 TDM-GCC（安装时选择"Add to PATH"）

3. 验证安装（同上）

### 方法 C：Chocolatey（自动化安装）

如果你已安装 Chocolatey 包管理器：

```powershell
choco install mingw
```

---

## 3. 安装 Boehm GC（可选）

Boehm GC 是一个垃圾回收库，Just 语言可以使用它来管理内存。

**当前版本暂不需要**，运行时库使用简单的 malloc/free。

未来版本如需集成：

```bash
# MSYS2
pacman -S mingw-w64-x86_64-gc

# 或从源码编译
# https://github.com/ivmai/bdwgc
```

---

## 4. 验证安装

打开 PowerShell 或 CMD，运行：

```bash
java -version
javac -version
gcc --version
```

应该看到类似输出：

```
java version "17.0.x"
javac 17.0.x
gcc (x86_64-win32-seh-rev0, Built by MinGW-W64 project) 12.2.0
```

---

## 5. 测试 Just 编译器

1. 克隆或下载 Just 语言仓库

2. 进入 `just-lang` 目录

3. 运行测试：
   ```bash
   justc.bat examples\hello.just
   ```

4. 如果成功，会生成 `hello.exe`，运行：
   ```bash
   hello.exe
   ```

---

## 常见问题

### Q1: "javac 不是内部或外部命令"

**原因**：Java 未添加到 PATH

**解决方法**：
1. 确认 JDK 已安装
2. 按上述步骤配置 `JAVA_HOME` 和 `Path`
3. **重启 PowerShell/CMD**

### Q2: "gcc 不是内部或外部命令"

**原因**：GCC 未添加到 PATH

**解决方法**：
1. 确认 GCC 已安装
2. 找到 GCC 安装目录（如 `C:\msys64\mingw64\bin`）
3. 添加到系统 `Path`
4. **重启 PowerShell/CMD**

### Q3: 编译 Just 编译器失败

**错误信息**：
```
错误: 编码 GBK 的不可映射字符
```

**解决方法**：
在 `justc.bat` 中的 `javac` 命令已添加 `-encoding UTF-8`，如果仍有问题，手动编译：

```bash
cd compiler
javac -d bin -encoding UTF-8 src/just/**/*.java
```

### Q4: GCC 编译时报错找不到 runtime.h

**原因**：编译命令缺少包含路径

**解决方法**：
手动编译时添加 `-I` 参数：
```bash
gcc -o hello.exe hello.c runtime\runtime.c -Iruntime
```

---

## 下一步

安装完成后，查看以下文档：

- **快速入门**：`REFERENCE.md` - 语法快速参考
- **示例代码**：`examples/` 目录
- **开发路线图**：`ROADMAP.md`

---

## 需要帮助？

如果遇到问题：

1. 检查 Java 和 GCC 是否正确安装
2. 确认环境变量已配置且已重启终端
3. 查看编译器错误信息
4. 提交 Issue 到项目仓库

---

**祝你使用愉快！🎉**
