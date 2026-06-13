// Just 2.0 概念演示 - 可空类型和安全访问

class User {
    string name;
    int age;

    User(string n, int a) {
        name = n;
        age = a;
    }

    string getName() {
        return name;
    }

    int getAge() {
        return age;
    }
}

class Optional {
    int hasValue;
    User value;

    Optional() {
        hasValue = 0;
    }

    void setValue(User v) {
        value = v;
        hasValue = 1;
    }

    int isEmpty() {
        if (hasValue == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    User getValue() {
        return value;
    }
}

class Result {
    int success;
    string errorMessage;

    Result(int s) {
        success = s;
    }

    void setError(string msg) {
        errorMessage = msg;
        success = 0;
    }

    int isSuccess() {
        return success;
    }
}

class Main {
    void main() {
        println("=== Just 2.0 - Advanced Features Demo ===");
        println("");

        // 1. Optional 模式（模拟可空类型）
        println("1. Optional pattern (nullable types):");
        Optional maybeUser = new Optional();

        if (maybeUser.isEmpty() == 1) {
            println("User is not present");
        } else {
            println("User is present");
        }

        // 设置值
        User user = new User("Alice", 25);
        maybeUser.setValue(user);

        if (maybeUser.isEmpty() == 0) {
            println("User is now present!");
        } else {
            println("Still empty");
        }

        println("");

        // 2. Result 模式（错误处理）
        println("2. Result pattern (error handling):");
        Result result = new Result(1);

        if (result.isSuccess() == 1) {
            println("Operation succeeded");
        } else {
            println("Operation failed");
        }

        // 模拟错误
        result.setError("Network timeout");

        if (result.isSuccess() == 0) {
            println("Operation failed with error");
        } else {
            println("Still success");
        }

        println("");
        println("=== Advanced patterns demonstrated! ===");
    }
}
