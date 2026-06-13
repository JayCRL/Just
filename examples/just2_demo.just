// Just 2.0 特性演示（使用当前支持的语法）

// 模拟泛型：为每种类型创建专门的类
class IntBox {
    int value;

    IntBox(int v) {
        value = v;
    }

    int get() {
        return value;
    }

    void set(int v) {
        value = v;
    }
}

class StringBox {
    string value;

    StringBox(string v) {
        value = v;
    }

    string get() {
        return value;
    }

    void set(string v) {
        value = v;
    }
}

class Main {
    void main() {
        println("=== Just 2.0 Feature Demo ===");
        println("");

        println("1. Generic-like classes:");
        IntBox intBox = new IntBox(42);
        int num = intBox.get();
        println("IntBox created and accessed");

        println("");
        println("2. Object-oriented programming:");
        intBox.set(100);
        println("Value updated via method call");

        println("");
        println("=== Demo complete! ===");
    }
}
