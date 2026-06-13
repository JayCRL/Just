/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Task.Task */
Task* Task_Task(int taskId) {
    Task* self = (Task*)just_alloc(sizeof(Task));
    self->id = taskId;
    self->status = 0;
    return self;
}

/* Task.start */
void Task_start(Task* self) {
    self->status = 1;
    just_println_string("Task started");
}

/* Task.complete */
void Task_complete(Task* self) {
    self->status = 2;
    just_println_string("Task completed");
}

/* Counter.Counter */
Counter* Counter_Counter() {
    Counter* self = (Counter*)just_alloc(sizeof(Counter));
    self->value = 0;
    return self;
}

/* Counter.increment */
void Counter_increment(Counter* self) {
    self->value = (self->value + 1);
}

/* Counter.display */
void Counter_display(Counter* self) {
    just_println_string("Counter incremented");
}

/* Main.main */
void Main_main(Main* self) {
    just_println_string("=== Just Language - Full Feature Demo ===");
    just_println_string("");
    just_println_string("1. Variables and arithmetic:");
    int a = 10;
    int b = 20;
    int sum = (a + b);
    int product = (a * b);
    just_println_string("Calculations done");
    just_println_string("");
    just_println_string("2. Control flow - if/else:");
    if ((sum > 25)) {
        just_println_string("Sum is large");
    } else {
        just_println_string("Sum is small");
    }
    just_println_string("");
    just_println_string("3. Control flow - while:");
    int counter = 0;
    while ((counter < 3)) {
        just_println_string("While iteration");
        counter = (counter + 1);
    }
    just_println_string("");
    just_println_string("4. Control flow - for:");
    for (int i = 0; (i < 2); i = (i + 1)) {
        just_println_string("For iteration");
    }
    just_println_string("");
    just_println_string("5. Object creation:");
    Counter* cnt = Counter_Counter();
    just_println_string("Counter created");
    just_println_string("");
    just_println_string("6. Method calls:");
    Counter_increment(cnt);
    Counter_increment(cnt);
    Counter_display(cnt);
    just_println_string("");
    just_println_string("7. Multiple objects:");
    Task* task1 = Task_Task(1);
    Task* task2 = Task_Task(2);
    Task_start(task1);
    Task_complete(task1);
    Task_start(task2);
    Task_complete(task2);
    just_println_string("");
    just_println_string("=== All features work perfectly! ===");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
