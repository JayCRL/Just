/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Point.Point */
Point* Point_Point(int px, int py) {
    Point* self = (Point*)just_alloc(sizeof(Point));
    self->x = px;
    self->y = py;
    return self;
}

/* Point.getX */
int Point_getX(Point* self) {
    return self->x;
}

/* Point.getY */
int Point_getY(Point* self) {
    return self->y;
}

/* Point.move */
void Point_move(Point* self, int dx, int dy) {
    self->x = (self->x + dx);
    self->y = (self->y + dy);
}

/* Point.distanceFromOrigin */
int Point_distanceFromOrigin(Point* self) {
    return ((self->x * self->x) + (self->y * self->y));
}

/* Status.Status */
Status* Status_Status(int c) {
    Status* self = (Status*)just_alloc(sizeof(Status));
    self->code = c;
    return self;
}

/* Status.isSuccess */
bool Status_isSuccess(Status* self) {
    if ((self->code == 0)) {
        return true;
    } else {
        return false;
    }
}

/* Status.isError */
bool Status_isError(Status* self) {
    if ((self->code != 0)) {
        return true;
    } else {
        return false;
    }
}

/* Counter.Counter */
Counter* Counter_Counter() {
    Counter* self = (Counter*)just_alloc(sizeof(Counter));
    self->count = 0;
    return self;
}

/* Counter.increment */
void Counter_increment(Counter* self) {
    self->count = (self->count + 1);
}

/* Counter.decrement */
void Counter_decrement(Counter* self) {
    self->count = (self->count - 1);
}

/* Counter.reset */
void Counter_reset(Counter* self) {
    self->count = 0;
}

/* Counter.getValue */
int Counter_getValue(Counter* self) {
    return self->count;
}

/* Counter.isZero */
bool Counter_isZero(Counter* self) {
    if ((self->count == 0)) {
        return true;
    } else {
        return false;
    }
}

/* Counter.isPositive */
bool Counter_isPositive(Counter* self) {
    if ((self->count > 0)) {
        return true;
    } else {
        return false;
    }
}

/* OptionalInt.OptionalInt */
OptionalInt* OptionalInt_OptionalInt() {
    OptionalInt* self = (OptionalInt*)just_alloc(sizeof(OptionalInt));
    self->hasValue = 0;
    self->value = 0;
    return self;
}

/* OptionalInt.setValue */
void OptionalInt_setValue(OptionalInt* self, int v) {
    self->value = v;
    self->hasValue = 1;
}

/* OptionalInt.isEmpty */
bool OptionalInt_isEmpty(OptionalInt* self) {
    if ((self->hasValue == 0)) {
        return true;
    } else {
        return false;
    }
}

/* OptionalInt.getOrDefault */
int OptionalInt_getOrDefault(OptionalInt* self, int defaultValue) {
    if ((self->hasValue == 1)) {
        return self->value;
    } else {
        return defaultValue;
    }
}

/* Main.main */
void Main_main(Main* self) {
    just_println_string("鈺斺晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?");
    just_println_string("鈺?  Just Language - Ultimate Demo       鈺?");
    just_println_string("鈺?  Showcasing All Features              鈺?");
    just_println_string("鈺氣晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 1. Basic Types & Operations 鈹?鈹?鈹?");
    int a = 42;
    int b = 13;
    int sum = (a + b);
    int product = (a * b);
    int difference = (a - b);
    just_println_string("Arithmetic operations: done");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 2. Control Flow - if/else 鈹?鈹?鈹?");
    if ((sum > 50)) {
        just_println_string("Sum is greater than 50");
    } else {
        just_println_string("Sum is 50 or less");
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 3. Control Flow - while loop 鈹?鈹?鈹?");
    int i = 0;
    while ((i < 3)) {
        just_println_string("While iteration");
        i = (i + 1);
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 4. Control Flow - for loop 鈹?鈹?鈹?");
    for (int j = 0; (j < 3); j = (j + 1)) {
        just_println_string("For iteration");
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 5. Objects & Methods 鈹?鈹?鈹?");
    Point* p = Point_Point(10, 20);
    just_println_string("Point created at (10, 20)");
    Point_move(p, 5, 5);
    just_println_string("Point moved to (15, 25)");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 6. Method Return Values 鈹?鈹?鈹?");
    int px = Point_getX(p);
    int py = Point_getY(p);
    just_println_string("Got point coordinates");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 7. Counter Demo 鈹?鈹?鈹?");
    Counter* counter = Counter_Counter();
    Counter_increment(counter);
    Counter_increment(counter);
    Counter_increment(counter);
    just_println_string("Counter incremented 3 times");
    if (Counter_isPositive(counter)) {
        just_println_string("Counter is positive");
    }
    Counter_decrement(counter);
    just_println_string("Counter decremented");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 8. State Management 鈹?鈹?鈹?");
    Status* ok = Status_Status(0);
    Status* err = Status_Status(1);
    if (Status_isSuccess(ok)) {
        just_println_string("Status: SUCCESS");
    }
    if (Status_isError(err)) {
        just_println_string("Status: ERROR");
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 9. Optional Pattern 鈹?鈹?鈹?");
    OptionalInt* maybe = OptionalInt_OptionalInt();
    if (OptionalInt_isEmpty(maybe)) {
        just_println_string("Optional is empty");
    }
    OptionalInt_setValue(maybe, 100);
    if (OptionalInt_isEmpty(maybe)) {
        just_println_string("Still empty");
    } else {
        just_println_string("Optional now has a value");
    }
    int val = OptionalInt_getOrDefault(maybe, 0);
    just_println_string("Got value from optional");
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 10. Complex Expressions 鈹?鈹?鈹?");
    int complex = (((a * b) + sum) - difference);
    bool condition = ((sum > 30) && (product < 1000));
    if (condition) {
        just_println_string("Complex condition is true");
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 11. Nested Control Flow 鈹?鈹?鈹?");
    for (int x = 0; (x < 2); x = (x + 1)) {
        if ((x == 0)) {
            just_println_string("First iteration");
        } else {
            just_println_string("Second iteration");
        }
    }
    just_println_string("");
    just_println_string("鈹?鈹?鈹? 12. Multiple Object Interaction 鈹?鈹?鈹?");
    Point* p1 = Point_Point(0, 0);
    Point* p2 = Point_Point(10, 10);
    Point* p3 = Point_Point(5, 5);
    Point_move(p1, 1, 1);
    Point_move(p2, 2, 2);
    Point_move(p3, 3, 3);
    just_println_string("Three points created and moved");
    just_println_string("");
    just_println_string("鈺斺晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?");
    just_println_string("鈺?  鉁? All Features Demonstrated!        鈺?");
    just_println_string("鈺?  鉁? Compilation: Successful            鈺?");
    just_println_string("鈺?  鉁? Type Safety: Verified              鈺?");
    just_println_string("鈺?  鉁? Performance: Optimized             鈺?");
    just_println_string("鈺氣晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺愨晲鈺?");
    just_println_string("");
    just_println_string("Just Language - Simple, Fast, Safe!");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
