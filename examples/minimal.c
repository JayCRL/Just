/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Main.Main */
Main* Main_Main() {
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
