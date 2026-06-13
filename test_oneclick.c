/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* Greeting.sayHello */
void Greeting_sayHello(Greeting* self) {
    just_println_string("Hello from Just!");
    just_println_string("One-click compilation works!");
}

/* Greeting.Greeting (默认构造函数) */
Greeting* Greeting_Greeting() {
    Greeting* self = (Greeting*)just_alloc(sizeof(Greeting));
    return self;
}

/* Main.main */
void Main_main(Main* self) {
    Greeting* g = Greeting_Greeting();
    Greeting_sayHello(g);
}

/* Main.Main (默认构造函数) */
Main* Main_Main() {
    Main* self = (Main*)just_alloc(sizeof(Main));
    return self;
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
