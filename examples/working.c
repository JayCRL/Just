/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Calculator.add */
int Calculator_add(Calculator* self, int a, int b) {
    return (a + b);
}

/* Calculator.multiply */
int Calculator_multiply(Calculator* self, int a, int b) {
    return (a * b);
}

/* Main.main */
void Main_main(Main* self) {
    int x = 10;
    int y = 20;
    int sum = (x + y);
    int product = (x * y);
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
