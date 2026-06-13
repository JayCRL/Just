/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Main.main */
void Main_main(Main* self) {
    just_println_string("Testing control flow:");
    int x = 10;
    if ((x > 5)) {
        just_println_string("x is greater than 5");
    } else {
        just_println_string("x is not greater than 5");
    }
    int i = 0;
    while ((i < 3)) {
        just_println_string("Loop iteration");
        i = (i + 1);
    }
    just_println_string("Control flow test complete!");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
