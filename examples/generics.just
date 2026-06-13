// Just 2.0 泛型示例

class Box<T> {
    T value;

    Box(T v) {
        value = v;
    }

    T get() {
        return value;
    }

    void set(T v) {
        value = v;
    }
}

class Main {
    void main() {
        println("Testing generics:");

        // 使用泛型类
        Box<int> intBox = new Box<int>(42);
        int num = intBox.get();

        Box<string> strBox = new Box<string>("Hello");
        string text = strBox.get();

        println("Generics test complete!");
    }
}
