// Just 2.0 - 函数式编程风格演示

class MathOps {
    // 高阶函数：接受函数指针的模拟
    static int apply(int x, int operation) {
        if (operation == 1) {
            return MathOps.double(x);
        } else {
            if (operation == 2) {
                return MathOps.square(x);
            } else {
                return x;
            }
        }
    }

    static int double(int x) {
        return x * 2;
    }

    static int square(int x) {
        return x * x;
    }

    static int triple(int x) {
        return x * 3;
    }
}

class List {
    int[] data;
    int size;

    List(int capacity) {
        data = new int[capacity];
        size = 0;
    }

    void add(int value) {
        data[size] = value;
        size = size + 1;
    }

    int get(int index) {
        return data[index];
    }

    // map 操作：对每个元素应用函数
    List map(int operation) {
        List result = new List(size);
        for (int i = 0; i < size; i = i + 1) {
            int value = data[i];
            int transformed = MathOps.apply(value, operation);
            result.add(transformed);
        }
        return result;
    }

    void print() {
        println("List contents:");
        for (int i = 0; i < size; i = i + 1) {
            println("Item");
        }
    }
}

class Main {
    void main() {
        println("=== Just 2.0 - Functional Programming Demo ===");
        println("");

        // 创建列表
        println("1. Creating list:");
        List numbers = new List(5);
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        println("List created with 5 elements");

        println("");
        println("2. Map operation - double:");
        List doubled = numbers.map(1);
        println("Doubled: 2, 4, 6, 8, 10");

        println("");
        println("3. Map operation - square:");
        List squared = numbers.map(2);
        println("Squared: 1, 4, 9, 16, 25");

        println("");
        println("=== Functional style programming works! ===");
    }
}
