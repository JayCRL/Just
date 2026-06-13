/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Main.main */
void Main_main(Main* self) {
    just_println_string("Hello, Just!");
    int a = 10;
    int b = 20;
    int sum = (a + b);
    printf("%d\n", just_string_concat());
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
