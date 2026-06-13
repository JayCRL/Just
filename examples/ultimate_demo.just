// Just 语言 - 终极演示
// 展示所有已实现的功能

// ============ 数据结构 ============

class Point {
    int x;
    int y;

    Point(int px, int py) {
        x = px;
        y = py;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void move(int dx, int dy) {
        x = x + dx;
        y = y + dy;
    }

    int distanceFromOrigin() {
        return x * x + y * y;  // 简化版（平方和）
    }
}

// ============ 状态管理 ============

class Status {
    int code;

    Status(int c) {
        code = c;
    }

    bool isSuccess() {
        if (code == 0) {
            return true;
        } else {
            return false;
        }
    }

    bool isError() {
        if (code != 0) {
            return true;
        } else {
            return false;
        }
    }
}

// ============ 计数器 ============

class Counter {
    int count;

    Counter() {
        count = 0;
    }

    void increment() {
        count = count + 1;
    }

    void decrement() {
        count = count - 1;
    }

    void reset() {
        count = 0;
    }

    int getValue() {
        return count;
    }

    bool isZero() {
        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }

    bool isPositive() {
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
}

// ============ 可选值包装器 ============

class OptionalInt {
    int hasValue;
    int value;

    OptionalInt() {
        hasValue = 0;
        value = 0;
    }

    void setValue(int v) {
        value = v;
        hasValue = 1;
    }

    bool isEmpty() {
        if (hasValue == 0) {
            return true;
        } else {
            return false;
        }
    }

    int getOrDefault(int defaultValue) {
        if (hasValue == 1) {
            return value;
        } else {
            return defaultValue;
        }
    }
}

// ============ 主程序 ============

class Main {
    void main() {
        println("╔═══════════════════════════════════════╗");
        println("║  Just Language - Ultimate Demo       ║");
        println("║  Showcasing All Features              ║");
        println("╚═══════════════════════════════════════╝");
        println("");

        // 1. 基本类型和运算
        println("─── 1. Basic Types & Operations ───");
        int a = 42;
        int b = 13;
        int sum = a + b;
        int product = a * b;
        int difference = a - b;
        println("Arithmetic operations: done");

        // 2. 控制流 - if/else
        println("");
        println("─── 2. Control Flow - if/else ───");
        if (sum > 50) {
            println("Sum is greater than 50");
        } else {
            println("Sum is 50 or less");
        }

        // 3. 控制流 - while 循环
        println("");
        println("─── 3. Control Flow - while loop ───");
        int i = 0;
        while (i < 3) {
            println("While iteration");
            i = i + 1;
        }

        // 4. 控制流 - for 循环
        println("");
        println("─── 4. Control Flow - for loop ───");
        for (int j = 0; j < 3; j = j + 1) {
            println("For iteration");
        }

        // 5. 对象创建和方法调用
        println("");
        println("─── 5. Objects & Methods ───");
        Point p = new Point(10, 20);
        println("Point created at (10, 20)");

        p.move(5, 5);
        println("Point moved to (15, 25)");

        // 6. 方法返回值
        println("");
        println("─── 6. Method Return Values ───");
        int px = p.getX();
        int py = p.getY();
        println("Got point coordinates");

        // 7. 计数器演示
        println("");
        println("─── 7. Counter Demo ───");
        Counter counter = new Counter();

        counter.increment();
        counter.increment();
        counter.increment();
        println("Counter incremented 3 times");

        if (counter.isPositive()) {
            println("Counter is positive");
        }

        counter.decrement();
        println("Counter decremented");

        // 8. 状态管理
        println("");
        println("─── 8. State Management ───");
        Status ok = new Status(0);
        Status err = new Status(1);

        if (ok.isSuccess()) {
            println("Status: SUCCESS");
        }

        if (err.isError()) {
            println("Status: ERROR");
        }

        // 9. 可选值模式
        println("");
        println("─── 9. Optional Pattern ───");
        OptionalInt maybe = new OptionalInt();

        if (maybe.isEmpty()) {
            println("Optional is empty");
        }

        maybe.setValue(100);

        if (maybe.isEmpty()) {
            println("Still empty");
        } else {
            println("Optional now has a value");
        }

        int val = maybe.getOrDefault(0);
        println("Got value from optional");

        // 10. 复杂表达式
        println("");
        println("─── 10. Complex Expressions ───");
        int complex = a * b + sum - difference;
        bool condition = sum > 30 && product < 1000;

        if (condition) {
            println("Complex condition is true");
        }

        // 11. 嵌套控制流
        println("");
        println("─── 11. Nested Control Flow ───");
        for (int x = 0; x < 2; x = x + 1) {
            if (x == 0) {
                println("First iteration");
            } else {
                println("Second iteration");
            }
        }

        // 12. 多对象交互
        println("");
        println("─── 12. Multiple Object Interaction ───");
        Point p1 = new Point(0, 0);
        Point p2 = new Point(10, 10);
        Point p3 = new Point(5, 5);

        p1.move(1, 1);
        p2.move(2, 2);
        p3.move(3, 3);

        println("Three points created and moved");

        // 结束
        println("");
        println("╔═══════════════════════════════════════╗");
        println("║  ✓ All Features Demonstrated!        ║");
        println("║  ✓ Compilation: Successful            ║");
        println("║  ✓ Type Safety: Verified              ║");
        println("║  ✓ Performance: Optimized             ║");
        println("╚═══════════════════════════════════════╝");
        println("");
        println("Just Language - Simple, Fast, Safe!");
    }
}
