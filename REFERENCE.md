# Just 语言快速参考

## 基本语法

### 类定义
```java
class ClassName {
    // 字段
    private int field1;
    public string field2;
    
    // 构造函数
    ClassName(int field1, string field2);  // 简化语法，自动赋值
    
    // 或完整语法
    ClassName(int field1, string field2) {
        this.field1 = field1;
        this.field2 = field2;
    }
    
    // 方法
    void methodName() {
        // 方法体
    }
}
```

### 继承
```java
class Child extends Parent {
    Child(int x) {
        super(x);  // 调用父类构造函数
    }
    
    void method() {
        super.method();  // 调用父类方法
    }
}
```

### 操作符重载
```java
class Point {
    int x, y;
    
    Point operator+(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }
    
    bool operator==(Point other) {
        return this.x == other.x && this.y == other.y;
    }
}

// 使用
Point p3 = p1 + p2;
bool same = p1 == p2;
```

**支持的操作符：**
- 算术：`+` `-` `*` `/` `%`
- 比较：`==` `!=` `<` `>` `<=` `>=`
- 数组访问：`[]`

---

## 数据类型

### 基本类型
```java
int a = 10;              // 整数
long b = 1000000L;       // 长整数
float c = 3.14f;         // 单精度浮点
double d = 3.14159;      // 双精度浮点
bool flag = true;        // 布尔值
char ch = 'A';           // 字符
string s = "hello";      // 字符串
```

### 数组
```java
// 声明和初始化
int[] arr = [1, 2, 3, 4, 5];
string[] names = ["Alice", "Bob", "Charlie"];

// 访问
int first = arr[0];
arr[0] = 10;

// 长度
int len = arr.length;

// 动态分配
int[] arr2 = new int[10];
```

### 常量
```java
const int MAX_SIZE = 100;
const string APP_NAME = "Just";
```

---

## 控制流

### if-else
```java
if (condition) {
    // 代码
} else if (otherCondition) {
    // 代码
} else {
    // 代码
}
```

### while 循环
```java
while (condition) {
    // 代码
}
```

### 传统 for 循环
```java
for (int i = 0; i < 10; i++) {
    println(i);
}
```

### 数组遍历
```java
int[] arr = [1, 2, 3, 4, 5];
for (int x : arr) {
    println(x);
}
```

### 区间循环（数学表示法）
```java
// 左闭右开 [0, 10) → 0, 1, 2, ..., 9
for (int i : [0, 10)) {
    println(i);
}

// 闭区间 [0, 10] → 0, 1, 2, ..., 10
for (int i : [0, 10]) {
    println(i);
}

// 开区间 (0, 10) → 1, 2, 3, ..., 9
for (int i : (0, 10)) {
    println(i);
}

// 左开右闭 (0, 10] → 1, 2, 3, ..., 10
for (int i : (0, 10]) {
    println(i);
}

// 带步长 [0, 10, 2] → 0, 2, 4, 6, 8, 10
for (int i : [0, 10, 2]) {
    println(i);
}
```

### break 和 continue
```java
for (int i : [0, 10)) {
    if (i == 5) break;        // 跳出循环
    if (i % 2 == 0) continue; // 跳过本次
    println(i);
}
```

---

## 字符串

### 字符串插值
```java
string name = "Tom";
int age = 18;
println("我是 ${name}，今年 ${age} 岁");

// 表达式
int a = 10, b = 20;
println("sum = ${a + b}");

// 成员访问
Point p = new Point(1, 2);
println("x = ${p.x}");
```

### 字符串拼接
```java
string s1 = "Hello";
string s2 = "World";
string s3 = s1 + " " + s2;  // "Hello World"

// 自动类型转换
int num = 42;
string s = "The answer is " + num;  // "The answer is 42"
```

---

## 输入输出

### 输出
```java
print("Hello");      // 不换行
println("World");    // 换行
```

### 输入
```java
string name = input();       // 读取字符串
int age = inputInt();        // 读取整数
float price = inputFloat();  // 读取浮点数
```

---

## 对象和类

### 创建对象
```java
Point p = new Point(10, 20);
```

### 访问成员
```java
int x = p.x;       // 访问字段
p.display();       // 调用方法
```

### this 和 super
```java
class Child extends Parent {
    int value;
    
    Child(int value) {
        super(value);     // 调用父类构造函数
        this.value = value;  // this 指向当前对象
    }
    
    void method() {
        super.method();   // 调用父类方法
        this.display();   // 调用当前对象方法
    }
}
```

### null 检查
```java
Point p = null;
if (p == null) {
    println("对象为空");
}
```

---

## 运算符

### 算术运算符
```java
+ - * / %        // 加减乘除取模
++ --            // 自增自减
+= -=            // 复合赋值
```

### 比较运算符
```java
== != < > <= >=
```

### 逻辑运算符
```java
&&  // 逻辑与
||  // 逻辑或
!   // 逻辑非
```

### 赋值运算符
```java
=               // 赋值
+= -=           // 复合赋值
```

---

## 注释

### 单行注释
```java
// 这是单行注释
int x = 10;  // 行尾注释
```

### 多行注释
```java
/*
 * 这是多行注释
 * 可以跨越多行
 */
int y = 20;
```

---

## 完整示例

### Hello World
```java
class Main {
    void main() {
        println("Hello, Just!");
    }
}
```

### 计算器
```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }
    
    int multiply(int a, int b) {
        return a * b;
    }
}

class Main {
    void main() {
        Calculator calc = new Calculator();
        int sum = calc.add(10, 20);
        int product = calc.multiply(5, 6);
        println("Sum: ${sum}");
        println("Product: ${product}");
    }
}
```

### 点类（完整 OOP）
```java
class Point {
    private int x;
    private int y;
    
    Point(int x, int y);  // 自动赋值
    
    Point operator+(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }
    
    bool operator==(Point other) {
        return this.x == other.x && this.y == other.y;
    }
    
    void display() {
        println("Point(${this.x}, ${this.y})");
    }
    
    int getX() {
        return this.x;
    }
    
    void setX(int x) {
        this.x = x;
    }
}

class Main {
    void main() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);
        
        p1.display();
        p2.display();
        
        Point p3 = p1 + p2;
        println("p1 + p2 =");
        p3.display();
        
        if (p1 == p2) {
            println("相同");
        } else {
            println("不同");
        }
    }
}
```

### 数组操作
```java
class Main {
    void main() {
        int[] numbers = [5, 2, 8, 1, 9];
        
        // 遍历
        println("数组元素:");
        for (int n : numbers) {
            println(n);
        }
        
        // 求和
        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }
        println("总和: ${sum}");
        
        // 查找最大值
        int max = numbers[0];
        for (int i : [1, numbers.length)) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        println("最大值: ${max}");
    }
}
```

---

## 访问控制

```java
class Example {
    public int publicField;      // 任何地方可访问
    private int privateField;    // 只能类内访问
    int defaultField;            // 默认 public
}
```

**规则：**
- 不写修饰符 = `public`
- `private` 只能在类内部访问
- 没有 `protected`（暂时）

---

## 与 Java 的对比

| 特性 | Java | Just |
|------|------|------|
| 类声明 | `public class` | `class` |
| main 方法 | `public static void main(String[] args)` | `void main()` |
| 输出 | `System.out.println()` | `println()` |
| 输入 | `Scanner` | `input()` |
| 数组 | `new int[]{1,2,3}` | `[1, 2, 3]` |
| 字符串插值 | ❌ | ✅ `"${var}"` |
| 区间循环 | ❌ | ✅ `[0, 10)` |
| 操作符重载 | ❌ | ✅ |
| 构造函数简化 | ❌ | ✅ |
| 常量 | `final` | `const` |

---

**更多示例请查看 `examples/` 目录。**
