// Just 2.0 - 简化的类型安全模式演示

class TrafficLight {
    int state;  // 0=RED, 1=YELLOW, 2=GREEN

    TrafficLight(int s) {
        state = s;
    }

    void setState(int s) {
        state = s;
    }

    void setRed() {
        state = 0;
    }

    void setYellow() {
        state = 1;
    }

    void setGreen() {
        state = 2;
    }

    bool isRed() {
        if (state == 0) {
            return true;
        } else {
            return false;
        }
    }

    bool isYellow() {
        if (state == 1) {
            return true;
        } else {
            return false;
        }
    }

    bool isGreen() {
        if (state == 2) {
            return true;
        } else {
            return false;
        }
    }

    void display() {
        if (state == 0) {
            println("Traffic light: RED - STOP");
        } else {
            if (state == 1) {
                println("Traffic light: YELLOW - CAUTION");
            } else {
                println("Traffic light: GREEN - GO");
            }
        }
    }
}

class OrderStatus {
    int code;  // 0=PENDING, 1=PROCESSING, 2=SHIPPED, 3=DELIVERED

    OrderStatus() {
        code = 0;  // 默认 PENDING
    }

    void setPending() {
        code = 0;
    }

    void setProcessing() {
        code = 1;
    }

    void setShipped() {
        code = 2;
    }

    void setDelivered() {
        code = 3;
    }

    bool isPending() {
        if (code == 0) {
            return true;
        } else {
            return false;
        }
    }

    bool isDelivered() {
        if (code == 3) {
            return true;
        } else {
            return false;
        }
    }

    void display() {
        if (code == 0) {
            println("Order status: PENDING");
        } else {
            if (code == 1) {
                println("Order status: PROCESSING");
            } else {
                if (code == 2) {
                    println("Order status: SHIPPED");
                } else {
                    println("Order status: DELIVERED");
                }
            }
        }
    }
}

class Main {
    void main() {
        println("=== Just 2.0 - Type-Safe State Pattern ===");
        println("");

        // 1. 交通灯状态机
        println("1. Traffic Light State Machine:");
        TrafficLight light = new TrafficLight(0);
        light.display();

        light.setGreen();
        light.display();

        light.setYellow();
        light.display();

        light.setRed();
        light.display();

        println("");

        // 2. 订单状态跟踪
        println("2. Order Status Tracking:");
        OrderStatus order = new OrderStatus();
        order.display();

        order.setProcessing();
        order.display();

        order.setShipped();
        order.display();

        order.setDelivered();
        order.display();

        // 3. 状态检查
        println("");
        println("3. State Checking:");
        if (order.isDelivered()) {
            println("Order has been delivered!");
        }

        if (light.isRed()) {
            println("Traffic light is red, please stop");
        }

        println("");
        println("=== Type-safe state management works! ===");
    }
}
