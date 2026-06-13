class Calculator {
    int value;

    Calculator(int v) {
        value = v;
    }

    int add(int x) {
        return value + x;
    }

    int multiply(int x) {
        return value * x;
    }
}

class Main {
    void main() {
        println("=== Just Language Comprehensive Test ===");
        println("");

        println("1. Variables and arithmetic:");
        int a = 10;
        int b = 20;
        int sum = a + b;
        println("Done with arithmetic");

        println("");
        println("2. Control flow - if/else:");
        if (sum > 25) {
            println("Sum is greater than 25");
        } else {
            println("Sum is not greater than 25");
        }

        println("");
        println("3. Control flow - while loop:");
        int counter = 0;
        while (counter < 3) {
            println("Loop iteration");
            counter = counter + 1;
        }

        println("");
        println("4. Control flow - for loop:");
        for (int i = 0; i < 3; i = i + 1) {
            println("For loop iteration");
        }

        println("");
        println("5. Object creation:");
        Calculator calc = new Calculator(100);
        println("Calculator created");

        println("");
        println("6. Method calls:");
        int result1 = calc.add(50);
        int result2 = calc.multiply(2);
        println("Method calls complete");

        println("");
        println("=== All tests passed! ===");
    }
}
