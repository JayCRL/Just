class Calculator {
    int value;

    Calculator(int v) {
        value = v;
    }

    int add(int x) {
        return value + x;
    }
}

class Main {
    void main() {
        println("Testing object creation:");
        Calculator calc = new Calculator(100);
        println("Calculator created successfully!");

        println("");
        println("Testing for loop:");
        for (int i = 0; i < 3; i = i + 1) {
            println("Iteration");
        }

        println("");
        println("All tests passed!");
    }
}
