/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Main.main */
void Main_main(Main* self) {
    just_println_string("Hello, Just Language!");
    just_println_string("Testing integers:");
    int x = 42;
    int y = 100;
    int result = (x + y);
    just_println_string("Done!");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
