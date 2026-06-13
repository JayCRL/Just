// Just 语言完整功能演示（简化版）

class Task {
    int id;
    int status;

    Task(int taskId) {
        id = taskId;
        status = 0;
    }

    void start() {
        status = 1;
        println("Task started");
    }

    void complete() {
        status = 2;
        println("Task completed");
    }
}

class Counter {
    int value;

    Counter() {
        value = 0;
    }

    void increment() {
        value = value + 1;
    }

    void display() {
        println("Counter incremented");
    }
}

class Main {
    void main() {
        println("=== Just Language - Full Feature Demo ===");
        println("");

        // 1. 变量和基本运算
        println("1. Variables and arithmetic:");
        int a = 10;
        int b = 20;
        int sum = a + b;
        int product = a * b;
        println("Calculations done");

        // 2. 控制流 - if/else
        println("");
        println("2. Control flow - if/else:");
        if (sum > 25) {
            println("Sum is large");
        } else {
            println("Sum is small");
        }

        // 3. 控制流 - while 循环
        println("");
        println("3. Control flow - while:");
        int counter = 0;
        while (counter < 3) {
            println("While iteration");
            counter = counter + 1;
        }

        // 4. 控制流 - for 循环
        println("");
        println("4. Control flow - for:");
        for (int i = 0; i < 2; i = i + 1) {
            println("For iteration");
        }

        // 5. 对象创建
        println("");
        println("5. Object creation:");
        Counter cnt = new Counter();
        println("Counter created");

        // 6. 方法调用
        println("");
        println("6. Method calls:");
        cnt.increment();
        cnt.increment();
        cnt.display();

        // 7. 多对象交互
        println("");
        println("7. Multiple objects:");
        Task task1 = new Task(1);
        Task task2 = new Task(2);

        task1.start();
        task1.complete();

        task2.start();
        task2.complete();

        println("");
        println("=== All features work perfectly! ===");
    }
}
