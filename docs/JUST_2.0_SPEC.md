# Just 2.0 语言规范

## 概述

Just 2.0 是 Just 语言的下一个主要版本，增加了现代编程语言的高级特性。

---

## 1. 泛型 (Generics)

### 语法

```just
// 泛型类
class Box<T> {
    T value;
    
    Box(T v) {
        value = v;
    }
    
    T getValue() {
        return value;
    }
    
    void setValue(T v) {
        value = v;
    }
}

// 使用
Box<int> intBox = new Box<int>(42);
Box<string> strBox = new Box<string>("hello");
```

### 多个类型参数

```just
class Pair<K, V> {
    K key;
    V value;
    
    Pair(K k, V v) {
        key = k;
        value = v;
    }
}

// 使用
Pair<string, int> pair = new Pair<string, int>("age", 25);
```

### 类型约束

```just
// T 必须实现 Comparable
class SortedList<T where T : Comparable> {
    T[] items;
    
    void add(T item) {
        // ...
    }
}
```

### 泛型方法

```just
class Utils {
    static <T> T max(T a, T b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
}

// 使用
int maxNum = Utils.max<int>(10, 20);
```

### 实现方式：单态化 (Monomorphization)

编译时为每个类型实例化生成专门的代码：

```c
// Box<int> 生成
typedef struct Box_int {
    int value;
} Box_int;

Box_int* Box_int_new(int v) {
    Box_int* self = malloc(sizeof(Box_int));
    self->value = v;
    return self;
}

// Box<string> 生成
typedef struct Box_string {
    char* value;
} Box_string;

Box_string* Box_string_new(char* v) {
    Box_string* self = malloc(sizeof(Box_string));
    self->value = v;
    return self;
}
```

**优势**：
- ✅ 零运行时开销
- ✅ 类型安全
- ✅ 内联优化

**劣势**：
- ⚠️ 代码膨胀（每个实例化生成新代码）
- ⚠️ 编译时间增加

---

## 2. 可空类型 (Nullable Types)

### 语法

```just
// 可空类型声明
string? maybeNull = null;
int? maybeNumber = null;

// 赋值
maybeNull = "hello";
maybeNumber = 42;
```

### 空检查

```just
string? name = getName();

if (name != null) {
    println(name);  // 自动解包
}
```

### 空合并运算符

```just
string? name = null;
string displayName = name ?? "Anonymous";  // "Anonymous"

name = "Alice";
displayName = name ?? "Anonymous";  // "Alice"
```

### 安全访问运算符

```just
class Person {
    string? name;
    Address? address;
}

class Address {
    string? city;
}

Person? person = getPerson();
string? city = person?.address?.city;  // 链式安全访问
```

### 强制解包

```just
string? name = "Alice";
string definite = name!;  // 运行时断言：name 不为 null
```

### 实现方式

```c
// string? 编译为
typedef struct Optional_string {
    char* value;
    int has_value;
} Optional_string;

Optional_string Optional_string_none() {
    Optional_string opt;
    opt.has_value = 0;
    opt.value = NULL;
    return opt;
}

Optional_string Optional_string_some(char* value) {
    Optional_string opt;
    opt.has_value = 1;
    opt.value = value;
    return opt;
}

// 空合并 (a ?? b)
char* str = opt.has_value ? opt.value : default_value;

// 强制解包 (a!)
if (!opt.has_value) {
    just_panic("Attempted to unwrap null value");
}
char* str = opt.value;
```

---

## 3. 异步/并发 (Async/Await)

### 语法

```just
// 异步函数
async fn fetchData(url: string) -> Future<string> {
    let response = await httpGet(url);
    return response.body;
}

// 使用
fn main() {
    let future = async fetchData("https://api.example.com");
    let data = await future;
    println(data);
}
```

### 并行执行

```just
async fn main() {
    // 启动多个异步操作
    let future1 = async fetchData("url1");
    let future2 = async fetchData("url2");
    let future3 = async fetchData("url3");
    
    // 等待所有完成
    let data1 = await future1;
    let data2 = await future2;
    let data3 = await future3;
}
```

### Channel 通信

```just
fn producer(ch: Channel<int>) {
    for (int i = 0; i < 10; i = i + 1) {
        ch.send(i);
    }
    ch.close();
}

fn consumer(ch: Channel<int>) {
    while (ch.isOpen()) {
        int? value = ch.receive();
        if (value != null) {
            println(value!);
        }
    }
}

fn main() {
    Channel<int> ch = new Channel<int>();
    async producer(ch);
    async consumer(ch);
}
```

### 实现方式：状态机

```c
// async fn fetchData() 编译为状态机
typedef struct FetchData_State {
    int state;
    char* url;
    HttpResponse* response;
} FetchData_State;

Future* fetchData(char* url) {
    FetchData_State* state = malloc(sizeof(FetchData_State));
    state->state = 0;
    state->url = url;
    return future_create(fetchData_resume, state);
}

void fetchData_resume(void* state_ptr) {
    FetchData_State* state = (FetchData_State*)state_ptr;
    
    switch (state->state) {
        case 0:
            // 启动 HTTP 请求
            state->state = 1;
            httpGet_async(state->url, fetchData_resume, state);
            return;
        
        case 1:
            // HTTP 请求完成
            state->response = httpGet_result();
            // 返回结果
            future_complete(state, state->response->body);
            return;
    }
}
```

---

## 4. 模式匹配 (Pattern Matching)

### 基本匹配

```just
match value {
    0 => println("zero"),
    1 => println("one"),
    2 => println("two"),
    _ => println("other")
}
```

### 带条件的匹配

```just
match number {
    n if n < 0 => println("negative"),
    0 => println("zero"),
    n if n > 0 && n < 10 => println("small positive"),
    _ => println("large positive")
}
```

### 类型匹配

```just
match obj {
    is int => println("integer"),
    is string => println("string"),
    is Person => println("person"),
    _ => println("unknown")
}
```

### 解构匹配

```just
class Point {
    int x;
    int y;
}

match point {
    Point(0, 0) => println("origin"),
    Point(x, 0) => println("on x-axis"),
    Point(0, y) => println("on y-axis"),
    Point(x, y) => println("general point")
}
```

### 实现方式

```c
// 简单匹配 → switch
switch (value) {
    case 0: printf("zero\n"); break;
    case 1: printf("one\n"); break;
    default: printf("other\n");
}

// 复杂匹配 → if-else 链
if (number < 0) {
    printf("negative\n");
} else if (number == 0) {
    printf("zero\n");
} else if (number > 0 && number < 10) {
    printf("small positive\n");
} else {
    printf("large positive\n");
}
```

---

## 5. Lambda 表达式

### 语法

```just
// 单表达式
(int x) => x * 2

// 多参数
(int a, int b) => a + b

// 代码块
(int x) => {
    int result = x * 2;
    return result;
}
```

### 使用示例

```just
class List<T> {
    T[] items;
    
    List<U> map<U>(Function<T, U> fn) {
        List<U> result = new List<U>();
        for (T item : items) {
            result.add(fn(item));
        }
        return result;
    }
}

// 使用
List<int> numbers = [1, 2, 3, 4, 5];
List<int> doubled = numbers.map((int x) => x * 2);
// [2, 4, 6, 8, 10]
```

### 闭包

```just
fn makeAdder(int x) -> Function<int, int> {
    return (int y) => x + y;  // 捕获 x
}

let add5 = makeAdder(5);
println(add5(10));  // 15
```

### 实现方式

**简单 Lambda (无闭包)**：
```c
// 编译为静态函数
int lambda_1(int x) {
    return x * 2;
}

typedef int (*Function_int_int)(int);
Function_int_int fn = lambda_1;
```

**闭包 Lambda**：
```c
// 使用结构体捕获变量
typedef struct Closure {
    int captured_x;  // 捕获的变量
    int (*fn)(struct Closure*, int);  // 函数指针
} Closure;

int lambda_adder_impl(Closure* self, int y) {
    return self->captured_x + y;
}

Closure* makeAdder(int x) {
    Closure* closure = malloc(sizeof(Closure));
    closure->captured_x = x;
    closure->fn = lambda_adder_impl;
    return closure;
}
```

---

## 6. 属性 (Properties)

### 语法

```just
class Person {
    private string _name;
    
    string name {
        get => _name;
        set => _name = value;
    }
}

// 使用
Person p = new Person();
p.name = "Alice";  // 调用 setter
println(p.name);   // 调用 getter
```

### 计算属性

```just
class Rectangle {
    int width;
    int height;
    
    int area {
        get => width * height;
    }
}
```

### 实现方式

```c
// 编译为 getter/setter 方法
char* Person_get_name(Person* self) {
    return self->_name;
}

void Person_set_name(Person* self, char* value) {
    self->_name = value;
}

// 使用
Person_set_name(p, "Alice");
printf("%s\n", Person_get_name(p));
```

---

## 7. 扩展方法 (Extension Methods)

### 语法

```just
// 为 int 添加扩展方法
extension int {
    bool isEven() {
        return this % 2 == 0;
    }
    
    bool isOdd() {
        return this % 2 != 0;
    }
}

// 使用
int num = 42;
if (num.isEven()) {
    println("even");
}
```

### 实现方式

```c
// 编译为静态函数
int int_isEven(int self) {
    return self % 2 == 0;
}

// 使用
if (int_isEven(num)) {
    printf("even\n");
}
```

---

## 8. 枚举 (Enums)

### 简单枚举

```just
enum Color {
    RED,
    GREEN,
    BLUE
}

// 使用
Color c = Color.RED;
```

### 带数据的枚举

```just
enum Option<T> {
    Some(T value),
    None
}

// 使用
Option<int> maybeNum = Option.Some(42);
Option<int> nothing = Option.None;

match maybeNum {
    Some(value) => println(value),
    None => println("no value")
}
```

### 实现方式

```c
// 简单枚举 → C enum
typedef enum Color {
    Color_RED,
    Color_GREEN,
    Color_BLUE
} Color;

// 带数据的枚举 → tagged union
typedef struct Option_int {
    enum { SOME, NONE } tag;
    union {
        int some_value;
    } data;
} Option_int;
```

---

## 性能对比

| 特性 | Just 2.0 | Rust | Go | C++ |
|------|---------|------|-----|-----|
| 泛型 | 单态化（零开销） | 单态化 | 类型擦除 | 模板 |
| 可空类型 | 编译时检查 | Option<T> | 无 | std::optional |
| 异步 | 状态机 | 状态机 | goroutines | 协程 |
| 模式匹配 | 编译时优化 | 完整支持 | 无 | 部分支持 |

---

## 实现优先级

1. ✅ **可空类型** - 最实用，相对简单
2. ✅ **泛型** - 核心特性，中等复杂
3. ⏳ **Lambda** - 实用，中等复杂
4. ⏳ **属性** - 语法糖，简单
5. ⏳ **枚举** - 实用，简单
6. ⏳ **模式匹配** - 复杂，但价值高
7. ⏳ **扩展方法** - 语法糖，简单
8. ⏳ **异步/并发** - 最复杂，价值极高

---

**Just 2.0 - 现代、快速、安全！** 🚀
