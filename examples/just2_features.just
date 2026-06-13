/*
 * Just 2.0 并发示例
 * 展示协程、Channel、泛型等新特性
 */

// ==================== 示例 1: 基础协程 ====================

class AsyncExample {
    void main() {
        println("=== 协程示例 ===");

        // 启动协程
        async {
            println("协程 1: 开始");
            sleep(100);
            println("协程 1: 完成");
        };

        async {
            println("协程 2: 开始");
            sleep(50);
            println("协程 2: 完成");
        };

        println("主线程继续");
        sleep(200);
    }
}

// ==================== 示例 2: Channel 通信 ====================

class ChannelExample {
    void main() {
        println("=== Channel 示例 ===");

        Channel<int> ch = new Channel<int>(10);

        // 生产者
        async {
            for (int i : [1, 10]) {
                ch.send(i);
                println("发送: ${i}");
                sleep(50);
            }
            ch.close();
        };

        // 消费者
        async {
            while (ch.isOpen()) {
                int value = ch.receive();
                if (value != null) {
                    println("接收: ${value}");
                }
            }
        };

        sleep(1000);
    }
}

// ==================== 示例 3: 泛型集合 ====================

class GenericExample {
    void main() {
        println("=== 泛型示例 ===");

        // List<int>
        List<int> numbers = new List<int>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        println("List 大小: ${numbers.size()}");

        // 高阶函数
        List<int> doubled = numbers.map(x => x * 2);
        doubled.forEach(x => println("翻倍: ${x}"));

        // Map<string, int>
        Map<string, int> ages = new Map<string, int>();
        ages["Alice"] = 25;
        ages["Bob"] = 30;

        println("Alice 的年龄: ${ages['Alice']}");

        // Set<string>
        Set<string> names = new Set<string>();
        names.add("Alice");
        names.add("Bob");
        names.add("Alice");  // 重复

        println("名字数量: ${names.size()}");
    }
}

// ==================== 示例 4: 并发下载器 ====================

class Downloader {
    private Channel<string> urls;
    private Mutex lock;
    private int completed;

    Downloader() {
        this.urls = new Channel<string>(100);
        this.lock = new Mutex();
        this.completed = 0;
    }

    void download(string[] urlList) {
        WaitGroup wg = new WaitGroup();

        // 启动 5 个工作协程
        for (int i : [1, 5]) {
            wg.add(1);
            async {
                this.worker(wg);
            };
        }

        // 发送所有 URL
        for (string url : urlList) {
            this.urls.send(url);
        }
        this.urls.close();

        // 等待完成
        wg.wait();
        println("所有下载完成！总计: ${this.completed}");
    }

    void worker(WaitGroup wg) {
        while (this.urls.isOpen()) {
            string url = this.urls.receive();
            if (url == null) break;

            // 模拟下载
            println("下载: ${url}");
            sleep(100);

            this.lock.acquire();
            this.completed++;
            this.lock.release();
        }

        wg.done();
    }
}

class Main {
    void main() {
        string[] urls = [
            "https://example.com/1",
            "https://example.com/2",
            "https://example.com/3",
            "https://example.com/4",
            "https://example.com/5"
        ];

        Downloader downloader = new Downloader();
        downloader.download(urls);
    }
}

// ==================== 示例 5: Future 异步结果 ====================

class FutureExample {
    void main() {
        println("=== Future 示例 ===");

        // 启动异步计算
        Future<int> future1 = async this.compute(100);
        Future<int> future2 = async this.compute(200);

        println("主线程继续工作...");

        // 等待结果
        int result1 = future1.wait();
        int result2 = future2.wait();

        println("结果: ${result1}, ${result2}");
        println("总和: ${result1 + result2}");
    }

    int compute(int n) {
        println("计算 ${n}...");
        sleep(200);
        return n * n;
    }
}

// ==================== 示例 6: 模式匹配 ====================

class PatternMatchingExample {
    void main() {
        println("=== 模式匹配示例 ===");

        // switch 表达式
        int score = 85;
        string grade = switch (score) {
            0..59 => "不及格",
            60..69 => "及格",
            70..79 => "中等",
            80..89 => "良好",
            90..100 => "优秀",
            _ => "无效"
        };

        println("分数 ${score} 的等级: ${grade}");

        // 类型匹配
        Object obj = "Hello";
        this.processValue(obj);

        obj = 42;
        this.processValue(obj);
    }

    void processValue(Object obj) {
        switch (obj) {
            case int n => println("整数: ${n}"),
            case string s => println("字符串: ${s}"),
            case null => println("空值"),
            _ => println("其他类型")
        }
    }
}

// ==================== 示例 7: 可空类型 ====================

class NullableExample {
    void main() {
        println("=== 可空类型示例 ===");

        string? name = this.getName();

        // 安全调用
        int? length = name?.length();
        println("长度: ${length}");

        // null 合并
        string displayName = name ?? "未知";
        println("显示名称: ${displayName}");

        // Optional
        Optional<int> opt = new Optional<int>(42);
        if (opt.isPresent()) {
            println("值: ${opt.get()}");
        }

        int value = opt.orElse(0);
        println("值或默认: ${value}");
    }

    string? getName() {
        // 50% 概率返回 null
        if (random() > 0.5) {
            return "Alice";
        }
        return null;
    }
}

// ==================== 示例 8: 扩展方法 ====================

extension int {
    bool isEven() {
        return this % 2 == 0;
    }

    bool isPrime() {
        if (this < 2) return false;
        for (int i : [2, this)) {
            if (this % i == 0) return false;
        }
        return true;
    }

    int factorial() {
        if (this <= 1) return 1;
        int result = 1;
        for (int i : [2, this + 1]) {
            result *= i;
        }
        return result;
    }
}

class ExtensionExample {
    void main() {
        println("=== 扩展方法示例 ===");

        int n = 42;
        println("${n} 是偶数: ${n.isEven()}");

        int p = 17;
        println("${p} 是质数: ${p.isPrime()}");

        int f = 5;
        println("${f} 的阶乘: ${f.factorial()}");
    }
}

// ==================== 示例 9: 属性 ====================

class Person {
    private string _name;
    private int _age;

    // 属性 - 带验证的 setter
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
    string displayName {
        get { return "Name: ${this._name}, Age: ${this._age}"; }
    }
}

class PropertyExample {
    void main() {
        println("=== 属性示例 ===");

        Person p = new Person();
        p.name = "Alice";  // 调用 setter
        p.age = 25;

        println("名字: ${p.name}");  // 调用 getter
        println("显示: ${p.displayName}");
    }
}

// ==================== 示例 10: struct（值类型）====================

struct Point {
    int x;
    int y;

    Point(int x, int y);

    int distanceSquared() {
        return this.x * this.x + this.y * this.y;
    }

    Point operator+(Point other) {
        return Point(this.x + other.x, this.y + other.y);
    }
}

class StructExample {
    void main() {
        println("=== Struct 示例 ===");

        Point p1 = Point(1, 2);
        Point p2 = p1;  // 值拷贝

        p2.x = 10;
        println("p1.x: ${p1.x}");  // 仍然是 1
        println("p2.x: ${p2.x}");  // 是 10

        Point p3 = p1 + p2;
        println("p3: (${p3.x}, ${p3.y})");
    }
}
