// Just 2.0 - 枚举模式演示

class ColorFactory {
    Color red;
    Color green;
    Color blue;

    ColorFactory() {
        red = new Color(0);
        green = new Color(1);
        blue = new Color(2);
    }

    Color getRed() {
        return red;
    }

    Color getGreen() {
        return green;
    }

    Color getBlue() {
        return blue;
    }
}

class Color {
    int value;

    Color(int v) {
        value = v;
    }

    bool isRed() {
        if (value == 0) {
            return true;
        } else {
            return false;
        }
    }

    bool isGreen() {
        if (value == 1) {
            return true;
        } else {
            return false;
        }
    }

    bool isBlue() {
        if (value == 2) {
            return true;
        } else {
            return false;
        }
    }

    int getValue() {
        return value;
    }
}

class StatusFactory {
    Status success;
    Status failure;
    Status pending;

    StatusFactory() {
        success = new Status(0);
        failure = new Status(1);
        pending = new Status(2);
    }

    Status getSuccess() {
        return success;
    }

    Status getFailure() {
        return failure;
    }

    Status getPending() {
        return pending;
    }
}

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

    bool isFailure() {
        if (code == 1) {
            return true;
        } else {
            return false;
        }
    }

    bool isPending() {
        if (code == 2) {
            return true;
        } else {
            return false;
        }
    }
}

class Main {
    void main() {
        println("=== Just 2.0 - Enum Pattern Demo ===");
        println("");

        println("1. Using Color enum:");
        ColorFactory colors = new ColorFactory();

        Color red = colors.getRed();
        Color green = colors.getGreen();
        Color blue = colors.getBlue();

        if (red.isRed()) {
            println("First color is RED");
        }

        if (green.isGreen()) {
            println("Second color is GREEN");
        }

        if (blue.isBlue()) {
            println("Third color is BLUE");
        }

        println("");
        println("2. Using Status enum:");
        StatusFactory statuses = new StatusFactory();

        Status success = statuses.getSuccess();
        Status failure = statuses.getFailure();
        Status pending = statuses.getPending();

        if (success.isSuccess()) {
            println("First status is SUCCESS");
        }

        if (failure.isFailure()) {
            println("Second status is FAILURE");
        }

        if (pending.isPending()) {
            println("Third status is PENDING");
        }

        println("");
        println("=== Enum pattern demonstrated! ===");
    }
}
