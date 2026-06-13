/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Calculator.Calculator */
Calculator* Calculator_Calculator(int v) {
    Calculator* self = (Calculator*)just_alloc(sizeof(Calculator));
    self->value = v;
    return self;
}

/* Calculator.add */
int Calculator_add(Calculator* self, int x) {
    return (self->value + x);
}

/* Main.main */
void Main_main(Main* self) {
    just_println_string("Testing object creation:");
    Calculator* calc = Calculator_Calculator(100);
    just_println_string("Calculator created successfully!");
    just_println_string("");
    just_println_string("Testing for loop:");
    for (int i = 0; (i < 3); i = (i + 1)) {
        just_println_string("Iteration");
    }
    just_println_string("");
    just_println_string("All tests passed!");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
