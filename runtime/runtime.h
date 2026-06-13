/* Just 语言运行时库 - 头文件 */
#ifndef JUST_RUNTIME_H
#define JUST_RUNTIME_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

/* 初始化运行时 */
void just_runtime_init();

/* 内存分配 */
void* just_alloc(size_t size);

/* 打印整数并换行 */
void just_println_int(int value);

/* 打印字符串并换行 */
void just_println_string(const char* str);

/* 运行时错误 */
void just_runtime_error(const char* message);

#endif
