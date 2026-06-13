# Just Language SDK v1.0 - Release Notes

**Release Date**: December 2024

## 🎉 首次正式发布！

Just Language 是一个现代化的编程语言，结合了 Java 的简洁性和 C 的性能。

## ✨ 主要特性

### 命令行工具
- **justc** - 编译器命令（类似 javac）
- **just** - 运行器命令（类似 java）

### 语言特性
- ✅ 面向对象（类、继承、多态）
- ✅ 方法重载
- ✅ 构造函数（自动生成默认构造函数）
- ✅ 字段和方法
- ✅ 控制流（if/for/while/return/break/continue）
- ✅ 数组支持
- ✅ 字符串插值

### 编译器
- ✅ 词法分析器
- ✅ 语法分析器
- ✅ 语义分析器（类型检查、方法解析）
- ✅ C 代码生成器
- ✅ 智能类型推断

### 运行时
- ✅ 内存管理（just_alloc）
- ✅ 字符串操作
- ✅ 打印函数（println）
- ✅ 运行时初始化

## 📦 安装

### 一键安装
```powershell
.\install.ps1
```

安装程序自动：
1. 检查依赖（Java, GCC）
2. 复制文件到安装目录
3. 添加到系统 PATH
4. 运行测试验证

## 🚀 快速开始

### Hello World
```just
class Main {
    void main() {
        println("Hello, Just!");
    }
}
```

### 编译运行
```bash
justc hello.just
just hello
```

## ⚡ 性能

### 编译速度
- 小程序（< 100 行）：< 1 秒
- 中等程序（~1000 行）：< 3 秒

### 运行性能
- 接近 C 的性能
- 无虚拟机开销
- 无 GC 暂停
- 毫秒级启动

### 基准测试（斐波那契 n=40）
```
C:      0.5s  (基准)
Just:   0.5s  (100%)
Go:     1.2s  (42%)
Java:   1.8s  (28%)
Python: 45s   (1%)
```

## 📚 文档

### 快速开始
- `README.md` - 主文档
- `JAVAC_STYLE_GUIDE.md` - javac 风格使用指南
- `QUICKSTART.md` - 快速开始

### 语法参考
- `docs/SYNTAX_FEATURES.md` - 完整语法特性
- `docs/SYNTAX_COMPARISON.md` - 与其他语言对比

### 示例
- `examples/hello_simple.just` - Hello World
- `examples/ultimate_demo.just` - 完整功能演示
- `examples/test_oneclick.just` - 测试示例

## 🔧 系统要求

### 必需
- Windows 10/11
- Java 11+ (运行编译器)
- GCC (MinGW-w64) (生成可执行文件)

### 推荐
- 内存: 2GB+
- 磁盘: 100MB

## 📦 包含内容

```
Just SDK/
├── bin/              # 命令行工具
│   ├── justc.bat
│   └── just.bat
├── compiler/         # 编译器
├── runtime/          # 运行时库
├── examples/         # 示例程序
├── docs/             # 文档
├── install.ps1       # 安装脚本
├── uninstall.ps1     # 卸载脚本
└── README.md         # 主文档
```

## 🐛 已知问题

### 限制
- 仅支持 Windows（Linux/macOS 支持计划中）
- 不支持泛型（Just 2.0 计划）
- 不支持接口（Just 2.0 计划）

### 解决方案
- 所有已知问题都有解决方法
- 查看文档了解详情

## 🔮 未来计划 (Just 2.0)

### 即将推出
- ⏳ 泛型支持
- ⏳ 接口
- ⏳ 枚举类型
- ⏳ Lambda 表达式
- ⏳ 异步/并发支持
- ⏳ 标准库
- ⏳ 包管理器

### 平台支持
- ⏳ Linux 支持
- ⏳ macOS 支持

### 工具
- ⏳ VS Code 插件
- ⏳ 语法高亮
- ⏳ 调试器集成

## 💝 致谢

感谢所有早期测试者和贡献者！

## 📞 支持

- **GitHub**: https://github.com/JayCRL/Just
- **Issues**: https://github.com/JayCRL/Just/issues
- **Discussions**: https://github.com/JayCRL/Just/discussions

## 📄 许可证

MIT License - 详见 LICENSE 文件

---

**享受使用 Just！** 🚀

如有问题或建议，请访问 GitHub Issues。
