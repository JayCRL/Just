# Just 2.0 完整特性指南

## 🚀 Just 2.0 新特性概览

Just 2.0 在保持 1.0 简洁哲学的基础上，添加了现代编程语言的核心特性。

### 新增特性

1. **并发支持** - 轻量级协程
2. **泛型系统** - 类型安全的泛型
3. **改进的 GC** - 三色并发标记
4. **LLVM 后端** - 更快的编译
5. **模式匹配** - 强大的 switch
6. **可空类型** - null 安全
7. **属性** - 智能的访问器
8. **扩展方法** - 为现有类型添加方法
9. **Lambda 表达式** - 函数式编程
10. **struct** - 值类型

---

## 1️⃣ 并发支持

### 协程（Coroutine）

```java
// 启动协程
async {
    println("在协程中运行");
    sleep(1000);
};

// async 函数调用
async downloadFile("url");

// 带返回值
Future<int> result = async compute(100);
int value = result.wait();
```

### Channel - 协程间通信

```java
Channel<int> ch = new Channel<int>(10);

// 发送
async {
    for (int i : [1, 10]) {
        ch.send(i);
    }
    ch.close();
};

// 接收
async {
    while (ch.isOpen()) {
        int value = ch.receive();
        println(value);
    }
};
```

### 同步原语

```java
// Mutex - 互斥锁
Mutex lock = new Mutex();
lock.acquire();
// 临界区
lock.release();

// synchronized 语法糖
synchronized void method() {
    // 自动加锁
}

// WaitGroup - 等待组
WaitGroup wg = new WaitGroup();
wg.add(3);

async { work1(); wg.done(); };
async { work2(); wg.done(); };
async { work3(); wg.done(); };

wg.wait();  // 等待全部完成
```

---

## 2️⃣ 泛型系统

### 泛型类

```java
class Box<T> {
    private T value;
    
    Box(T value) {
        this.value = value;
    }
    
    T get() {
        return this.value;
    }
}

// 使用
Box<int> intBox = new Box<int>(42);
Box<string> strBox = new Box<string>("hello");
```

### 泛型方法

```java
<T> T max(T a, T b) where T : Comparable<T> {
    if (a > b) return a;
    return b;
}

// 类型推导
int m = max(10, 20);       // T = int
string s = max("a", "b");  // T = string
```

### 类型约束

```java
// 单个约束
class List<T> where T : Comparable<T> {
    // ...
}

// 多重约束
class Sorter<T> where T : Comparable<T>, Cloneable {
    // ...
}

// 简写语法
class NumberList<T extends Number> {
    // T 必须是数字类型
}
```

### 标准泛型集合

```java
// List<T>
List<int> numbers = [1, 2, 3];
numbers.add(4);
int first = numbers[0];

// Map<K, V>
Map<string, int> ages = {
    "Alice": 25,
    "Bob": 30
};
int age = ages["Alice"];

// Set<T>
Set<string> names = {"Alice", "Bob"};
names.add("Charlie");
```

---

## 3️⃣ Lambda 表达式

```java
List<int> numbers = [1, 2, 3, 4, 5];

// 过滤
List<int> evens = numbers.filter(x => x % 2 == 0);

// 映射
List<int> doubled = numbers.map(x => x * 2);

// forEach
numbers.forEach(x => {
    println("数字: ${x}");
});

// 排序
numbers.sort((a, b) => a - b);
```

---

## 4️⃣ 模式匹配

### switch 表达式

```java
// 返回值的 switch
string grade = switch (score) {
    0..59 => "不及格",
    60..79 => "及格",
    80..89 => "良好",
    90..100 => "优秀",
    _ => "无效"
};

// 类型匹配
void process(Object obj) {
    switch (obj) {
        case int n => println("整数: ${n}"),
        case string s => println("字符串: ${s}"),
        case Point p => println("点: (${p.x}, ${p.y})"),
        case null => println("空"),
        _ => println("其他")
    }
}
```

---

## 5️⃣ 可空类型

```java
// ? 表示可空
string? name = null;  // 可以是 null
string realName = "Alice";  // 不能是 null

// 安全调用 ?.
int? length = name?.length();

// null 合并 ??
string displayName = name ?? "Unknown";

// 强制解包 !
string definite = name!;  // 如果是 null 会 panic

// Optional<T>
Optional<int> opt = new Optional<int>(42);
int value = opt.orElse(0);
```

---

## 6️⃣ 属性（Property）

```java
class Person {
    private string _name;
    
    // 完整属性
    string name {
        get { return this._name; }
        set(value) {
            if (value.length() > 0) {
                this._name = value;
            }
        }
    }
    
    // 自动属性
    int age { get; set; }
    
    // 只读属性
    string id { get; } = generateId();
}

// 使用
Person p = new Person();
p.name = "Alice";  // setter
string n = p.name;  // getter
```

---

## 7️⃣ 扩展方法

```java
// 为 int 添加方法
extension int {
    bool isEven() {
        return this % 2 == 0;
    }
    
    int squared() {
        return this * this;
    }
}

// 使用
int n = 42;
if (n.isEven()) {
    println("偶数");
}
println(n.squared());
```

---

## 8️⃣ struct（值类型）

```java
// struct - 栈分配
struct Point {
    int x;
    int y;
    
    Point(int x, int y);
    
    int distance() {
        return x * x + y * y;
    }
}

// 使用
Point p = Point(10, 20);  // 栈上
Point q = p;  // 值拷贝，不是引用
```

---

## 9️⃣ enum（枚举）

```java
// 简单枚举
enum Color {
    RED, GREEN, BLUE
}

// 带值的枚举
enum HttpStatus {
    OK(200),
    NOT_FOUND(404),
    SERVER_ERROR(500);
    
    private int code;
    
    HttpStatus(int code) {
        this.code = code;
    }
    
    int getCode() {
        return this.code;
    }
}

// 使用
Color c = Color.RED;
HttpStatus status = HttpStatus.OK;
println(status.getCode());
```

---

## 🔟 高级特性

### unsafe 块

```java
// 手动内存管理
unsafe {
    byte* ptr = malloc(1024);
    // 使用 ptr
    free(ptr);
}
```

### comptime（编译期计算）

```java
// 编译时常量
const int SIZE = comptime {
    int sum = 0;
    for (int i : [1, 100]) {
        sum += i;
    }
    return sum;
};  // SIZE = 5050
```

---

## 📚 完整示例

### 并发 Web 爬虫

```java
class WebCrawler {
    private Set<string> visited = new Set<string>();
    private Mutex lock = new Mutex();
    private Channel<string> urls = new Channel<string>(100);
    
    void crawl(string startUrl) {
        WaitGroup wg = new WaitGroup();
        
        // 10 个爬虫协程
        for (int i : [1, 10]) {
            wg.add(1);
            async worker(wg);
        }
        
        urls.send(startUrl);
        wg.wait();
        urls.close();
    }
    
    void worker(WaitGroup wg) {
        while (urls.isOpen()) {
            string? url = urls.receive();
            if (url == null) break;
            
            lock.acquire();
            if (visited.contains(url)) {
                lock.release();
                continue;
            }
            visited.add(url);
            lock.release();
            
            string content = download(url);
            List<string> links = extractLinks(content);
            
            links.forEach(link => urls.send(link));
        }
        wg.done();
    }
}
```

---

## 性能对比

| 特性 | Just 1.0 | Just 2.0 | Go |
|------|----------|----------|-----|
| 并发 | ❌ | ✅ 协程 | ✅ Goroutine |
| 泛型 | ❌ | ✅ 单态化 | ✅ |
| GC | 基础 | 三色并发 | 三色并发 |
| 编译速度 | 慢 | 快（LLVM） | 很快 |
| 启动速度 | 快 | 快 | 快 |
| 性能 | 好 | 很好 | 很好 |

**Just 2.0 目标：接近 Go 的性能，保持更简洁的语法。**

---

## 迁移指南（1.0 → 2.0）

### 完全兼容

Just 2.0 **完全向后兼容** Just 1.0 代码！

所有 1.0 特性在 2.0 中都可用：
- ✅ 类、继承、接口
- ✅ 操作符重载
- ✅ 字符串插值
- ✅ 区间循环
- ✅ 数组字面量

### 新增特性可选使用

```java
// 1.0 代码继续工作
class Point {
    int x, y;
    Point(int x, int y);
}

// 2.0 增强（可选）
class Point {
    int x, y;
    Point(int x, int y);
    
    // 新增：泛型方法
    <T> T process(T value) {
        return value;
    }
}
```

---

## 下一步

1. **安装 Just 2.0**：参见 `INSTALL.md`
2. **尝试示例**：`examples/just2_features.just`
3. **阅读 API 文档**：`stdlib/` 目录
4. **加入社区**：贡献代码和想法

---

**Just 2.0 - 简洁、快速、现代！** 🚀
