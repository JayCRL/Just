# Just 语言 - 当前支持的完整语法特性

## 📚 语法特性清单

---

## 1️⃣ 基本类型

### 原始类型
```just
int x = 42;
long bigNum = 1000000;
float pi = 3.14;
double precise = 3.14159265;
bool flag = true;
char letter = 'A';
string text = "Hello";
void func() { }
```

### 数组类型
```just
int[] numbers;
string[] names;
```

**支持状态**: ✅ 完全支持

---

## 2️⃣ 运算符

### 算术运算符
```just
int a = 10 + 5;   // 加法
int b = 10 - 5;   // 减法
int c = 10 * 5;   // 乘法
int d = 10 / 5;   // 除法
int e = 10 % 3;   // 取模
```

### 比较运算符
```just
bool eq = (a == b);   // 等于
bool ne = (a != b);   // 不等于
bool lt = (a < b);    // 小于
bool gt = (a > b);    // 大于
bool le = (a <= b);   // 小于等于
bool ge = (a >= b);   // 大于等于
```

### 逻辑运算符
```just
bool and = (a > 0 && b > 0);   // 逻辑与
bool or = (a > 0 || b > 0);    // 逻辑或
bool not = !(a > 0);           // 逻辑非
```

### 赋值运算符
```just
int x = 10;       // 赋值
x = x + 5;        // 也支持
x += 5;           // 复合赋值 ✅
x -= 3;           // 减法赋值 ✅
```

### 自增/自减
```just
int x = 0;
x++;    // 后缀自增 ✅
++x;    // 前缀自增 ✅
x--;    // 后缀自减 ✅
--x;    // 前缀自减 ✅
```

**支持状态**: ✅ 完全支持

---

## 3️⃣ 控制流

### if-else 语句
```just
if (x > 0) {
    println("positive");
} else {
    println("negative or zero");
}

// 嵌套 if
if (x > 0) {
    if (x < 10) {
        println("single digit");
    }
}
```

### while 循环
```just
int i = 0;
while (i < 10) {
    println("iteration");
    i = i + 1;
}
```

### for 循环（传统）
```just
for (int i = 0; i < 10; i = i + 1) {
    println("iteration");
}
```

### for 循环（增强型）
```just
int[] arr = new int[5];
for (int x : arr) {
    println("element");
}
```

### break 和 continue
```just
while (true) {
    if (condition) {
        break;      // 跳出循环 ✅
    }
    if (skip) {
        continue;   // 继续下一次 ✅
    }
}
```

### return 语句
```just
int getValue() {
    return 42;
}

void doSomething() {
    return;  // void 方法也可以 return
}
```

**支持状态**: ✅ 完全支持

---

## 4️⃣ 类和对象

### 类定义
```just
class Counter {
    int value;          // 字段
    string name;        // 多个字段
    
    // 构造函数
    Counter() {
        value = 0;
    }
    
    Counter(int v) {    // 构造函数重载 ✅
        value = v;
    }
    
    // 方法
    void increment() {
        value = value + 1;
    }
    
    int getValue() {
        return value;
    }
}
```

### 对象创建
```just
Counter c = new Counter();
Counter c2 = new Counter(100);
```

### 方法调用
```just
c.increment();              // void 方法
int v = c.getValue();       // 返回值方法 ✅
```

### 字段访问
```just
c.value = 10;               // 直接访问字段 ✅
int x = c.value;            // 读取字段 ✅
```

**支持状态**: ✅ 完全支持

---

## 5️⃣ 继承

### 类继承
```just
class Animal {
    string name;
    
    Animal(string n) {
        name = n;
    }
    
    void speak() {
        println("Animal speaks");
    }
}

class Dog extends Animal {
    Dog(string n) {
        super(n);  // 调用父类构造函数 ✅
    }
    
    void speak() {  // 方法重写 ✅
        println("Woof!");
    }
}
```

### super 关键字
```just
class Child extends Parent {
    void method() {
        super.parentMethod();  // 调用父类方法 ✅
    }
}
```

### this 关键字
```just
class MyClass {
    int value;
    
    void method() {
        this.value = 10;  // 显式 this ✅
        // 或者
        value = 10;       // 隐式访问 ✅
    }
}
```

**支持状态**: ✅ 完全支持

---

## 6️⃣ 访问修饰符

```just
class MyClass {
    public int x;       // 公共字段 ✅
    private int y;      // 私有字段 ✅
    
    public void method1() { }   // 公共方法 ✅
    private void method2() { }  // 私有方法 ✅
}
```

**支持状态**: ✅ 解析器支持，语义检查部分实现

---

## 7️⃣ 内置函数

### 输出函数
```just
println("Hello");           // 打印并换行 ✅
print("Hello");             // 打印不换行 ✅
```

### 类型转换（规划中）
```just
int x = parseInt("42");     // 字符串转整数 ⏳
float f = parseFloat("3.14"); // 字符串转浮点 ⏳
string s = toString(42);    // 整数转字符串 ⏳
```

### 输入函数（规划中）
```just
string input = readln();    // 读取一行 ⏳
```

**支持状态**: 
- ✅ println, print 完全支持
- ⏳ 其他函数已定义，未实现

---

## 8️⃣ 数组（部分支持）

### 数组声明
```just
int[] arr;              // 声明 ✅
int[] arr = new int[10]; // 创建 ✅
```

### 数组访问
```just
arr[0] = 42;            // 赋值 ✅
int x = arr[0];         // 读取 ✅
```

### 数组字面量（AST 支持，未完全测试）
```just
int[] numbers = [1, 2, 3, 4, 5];  // ⏳
```

**支持状态**: ⏳ 部分支持

---

## 9️⃣ 表达式

### 支持的表达式类型
```just
// 字面量
42                      // 整数 ✅
3.14                    // 浮点数 ✅
"hello"                 // 字符串 ✅
true                    // 布尔 ✅
null                    // 空值 ✅

// 二元表达式
x + y                   // ✅
x * y / z               // ✅
a && b || c             // ✅

// 一元表达式
-x                      // 负号 ✅
!flag                   // 逻辑非 ✅
++x                     // 自增 ✅

// 赋值表达式
x = 10                  // ✅
x += 5                  // ✅

// 方法调用
obj.method()            // ✅
obj.method(1, 2)        // 带参数 ✅

// 成员访问
obj.field               // ✅

// 数组索引
arr[i]                  // ✅

// new 表达式
new Counter()           // ✅
new int[10]             // ✅

// this 和 super
this.value              // ✅
super.method()          // ✅
```

**支持状态**: ✅ 完全支持

---

## 🔟 方法重载

```just
class Math {
    int add(int a, int b) {
        return a + b;
    }
    
    int add(int a, int b, int c) {  // 重载 ✅
        return a + b + c;
    }
}
```

**支持状态**: ✅ 完全支持

---

## 🚀 Just 2.0 特性（规划/部分实现）

### 泛型（设计完成，部分实现）
```just
class Box<T> {          // ⏳ AST 支持
    T value;
    
    T get() {
        return value;
    }
}

Box<int> intBox = new Box<int>();  // ⏳
```

**支持状态**: ⏳ 30% - AST 完成，代码生成未完成

### 可空类型（设计完成）
```just
string? nullable = null;        // ⏳
string value = nullable ?? "default";  // ⏳
string? result = obj?.method(); // ⏳
```

**支持状态**: ⏳ 0% - 仅设计

### Lambda 表达式（设计完成）
```just
(int x) => x * 2               // ⏳
numbers.map(x => x * 2)        // ⏳
```

**支持状态**: ⏳ 10% - AST 支持

### 异步/并发（设计完成）
```just
async fn fetchData() {         // ⏳
    let result = await httpGet("url");
    return result;
}
```

**支持状态**: ⏳ 15% - AST 支持

### 模式匹配（设计完成）
```just
match value {                  // ⏳
    0 => println("zero"),
    1 => println("one"),
    _ => println("other")
}
```

**支持状态**: ⏳ 10% - AST 支持

### 枚举（设计完成）
```just
enum Color {                   // ⏳
    RED,
    GREEN,
    BLUE
}
```

**支持状态**: ⏳ 0% - 关键字已定义

### 字符串插值（设计完成）
```just
string msg = "Hello, ${name}!"; // ⏳
```

**支持状态**: ⏳ 0% - 仅设计

---

## 📊 完整支持度总结

### ✅ 完全支持（100%）
- [x] 基本类型（int, long, float, double, bool, char, string, void）
- [x] 数组类型
- [x] 所有运算符
- [x] 所有控制流（if/else, while, for, break, continue, return）
- [x] 类和对象
- [x] 构造函数
- [x] 方法定义和调用
- [x] 字段访问
- [x] 继承
- [x] this 和 super
- [x] 方法重载
- [x] 内置函数（println, print）

### ⏳ 部分支持（30-90%）
- [ ] 数组字面量（AST 支持 ✅，测试不足）
- [ ] 访问修饰符（解析 ✅，检查部分）
- [ ] 泛型（设计 ✅，实现 30%）
- [ ] 其他内置函数（定义 ✅，未实现）

### ❌ 未实现（0-15%）
- [ ] 可空类型（设计完成）
- [ ] Lambda 表达式（AST 完成）
- [ ] 异步/并发（AST 完成）
- [ ] 模式匹配（AST 完成）
- [ ] 枚举（关键字定义）
- [ ] 字符串插值（设计完成）
- [ ] 属性（设计完成）
- [ ] 扩展方法（设计完成）

---

## 📈 总体完成度

```
Just 1.0 核心语法: ████████████████░ 95%
Just 2.0 高级特性: ███░░░░░░░░░░░░░░ 20%
---
总体完成度:       ████████████░░░░░ 60%
```

---

## 🎯 实际可用性

**当前可以做什么**：
- ✅ 写完整的面向对象程序
- ✅ 使用继承和多态
- ✅ 方法重载
- ✅ 所有控制流
- ✅ 数学计算
- ✅ 字符串处理
- ✅ 编译到高性能 C 代码

**目前还不能**：
- ❌ 泛型编程（进行中）
- ❌ 函数式编程（Lambda）
- ❌ 并发编程
- ❌ 高级字符串操作

---

## 📚 代码示例

### 完整可运行的 Just 程序
```just
class Calculator {
    int value;
    
    Calculator() {
        value = 0;
    }
    
    void add(int x) {
        value = value + x;
    }
    
    void subtract(int x) {
        value = value - x;
    }
    
    void multiply(int x) {
        value = value * x;
    }
    
    int getResult() {
        return value;
    }
}

class Main {
    void main() {
        println("Calculator Demo");
        
        Calculator calc = new Calculator();
        
        calc.add(10);
        calc.add(5);
        calc.multiply(2);
        calc.subtract(3);
        
        int result = calc.getResult();
        println("Result calculated");
        
        if (result > 20) {
            println("Result is large");
        } else {
            println("Result is small");
        }
    }
}
```

**这个程序可以**：
- ✅ 编译通过
- ✅ 生成 C 代码
- ✅ 用 GCC 编译
- ✅ 运行

---

## 🎓 总结

Just 当前是一个**功能完整的面向对象编程语言**，支持：
- ✅ 类、对象、继承
- ✅ 所有基本控制流
- ✅ 方法重载
- ✅ 编译到 C，性能优秀

Just 2.0 的高级特性正在开发中，但**现在就可以用于实际项目**！
