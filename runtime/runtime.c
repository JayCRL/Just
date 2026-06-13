/* Just 语言运行时库 - 实现 */
#include "runtime.h"

/* 初始化运行时 */
void just_runtime_init() {
    // 目前不需要特殊初始化
}

/* 内存分配 */
void* just_alloc(size_t size) {
    void* ptr = malloc(size);
    if (ptr == NULL) {
        just_runtime_error("Out of memory");
    }
    return ptr;
}

/* 打印整数并换行 */
void just_println_int(int value) {
    printf("%d\n", value);
}

/* 打印字符串并换行 */
void just_println_string(const char* str) {
    printf("%s\n", str);
}

/* 运行时错误 */
void just_runtime_error(const char* message) {
    fprintf(stderr, "Runtime Error: %s\n", message);
    exit(1);
}
