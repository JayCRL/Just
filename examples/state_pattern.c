/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* TrafficLight.TrafficLight */
TrafficLight* TrafficLight_TrafficLight(int s) {
    TrafficLight* self = (TrafficLight*)just_alloc(sizeof(TrafficLight));
    self->state = s;
    return self;
}

/* TrafficLight.setState */
void TrafficLight_setState(TrafficLight* self, int s) {
    self->state = s;
}

/* TrafficLight.setRed */
void TrafficLight_setRed(TrafficLight* self) {
    self->state = 0;
}

/* TrafficLight.setYellow */
void TrafficLight_setYellow(TrafficLight* self) {
    self->state = 1;
}

/* TrafficLight.setGreen */
void TrafficLight_setGreen(TrafficLight* self) {
    self->state = 2;
}

/* TrafficLight.isRed */
bool TrafficLight_isRed(TrafficLight* self) {
    if ((self->state == 0)) {
        return true;
    } else {
        return false;
    }
}

/* TrafficLight.isYellow */
bool TrafficLight_isYellow(TrafficLight* self) {
    if ((self->state == 1)) {
        return true;
    } else {
        return false;
    }
}

/* TrafficLight.isGreen */
bool TrafficLight_isGreen(TrafficLight* self) {
    if ((self->state == 2)) {
        return true;
    } else {
        return false;
    }
}

/* TrafficLight.display */
void TrafficLight_display(TrafficLight* self) {
    if ((self->state == 0)) {
        just_println_string("Traffic light: RED - STOP");
    } else {
        if ((self->state == 1)) {
            just_println_string("Traffic light: YELLOW - CAUTION");
        } else {
            just_println_string("Traffic light: GREEN - GO");
        }
    }
}

/* OrderStatus.OrderStatus */
OrderStatus* OrderStatus_OrderStatus() {
    OrderStatus* self = (OrderStatus*)just_alloc(sizeof(OrderStatus));
    self->code = 0;
    return self;
}

/* OrderStatus.setPending */
void OrderStatus_setPending(OrderStatus* self) {
    self->code = 0;
}

/* OrderStatus.setProcessing */
void OrderStatus_setProcessing(OrderStatus* self) {
    self->code = 1;
}

/* OrderStatus.setShipped */
void OrderStatus_setShipped(OrderStatus* self) {
    self->code = 2;
}

/* OrderStatus.setDelivered */
void OrderStatus_setDelivered(OrderStatus* self) {
    self->code = 3;
}

/* OrderStatus.isPending */
bool OrderStatus_isPending(OrderStatus* self) {
    if ((self->code == 0)) {
        return true;
    } else {
        return false;
    }
}

/* OrderStatus.isDelivered */
bool OrderStatus_isDelivered(OrderStatus* self) {
    if ((self->code == 3)) {
        return true;
    } else {
        return false;
    }
}

/* OrderStatus.display */
void OrderStatus_display(OrderStatus* self) {
    if ((self->code == 0)) {
        just_println_string("Order status: PENDING");
    } else {
        if ((self->code == 1)) {
            just_println_string("Order status: PROCESSING");
        } else {
            if ((self->code == 2)) {
                just_println_string("Order status: SHIPPED");
            } else {
                just_println_string("Order status: DELIVERED");
            }
        }
    }
}

/* Main.main */
void Main_main(Main* self) {
    just_println_string("=== Just 2.0 - Type-Safe State Pattern ===");
    just_println_string("");
    just_println_string("1. Traffic Light State Machine:");
    TrafficLight* light = TrafficLight_TrafficLight(0);
    TrafficLight_display(light);
    TrafficLight_setGreen(light);
    TrafficLight_display(light);
    TrafficLight_setYellow(light);
    TrafficLight_display(light);
    TrafficLight_setRed(light);
    TrafficLight_display(light);
    just_println_string("");
    just_println_string("2. Order Status Tracking:");
    OrderStatus* order = OrderStatus_OrderStatus();
    OrderStatus_display(order);
    OrderStatus_setProcessing(order);
    OrderStatus_display(order);
    OrderStatus_setShipped(order);
    OrderStatus_display(order);
    OrderStatus_setDelivered(order);
    OrderStatus_display(order);
    just_println_string("");
    just_println_string("3. State Checking:");
    if (OrderStatus_isDelivered(order)) {
        just_println_string("Order has been delivered!");
    }
    if (TrafficLight_isRed(light)) {
        just_println_string("Traffic light is red, please stop");
    }
    just_println_string("");
    just_println_string("=== Type-safe state management works! ===");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
