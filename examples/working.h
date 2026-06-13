/* Just 语言编译器生成的 C 代码 */
#ifndef JUST_GENERATED_H
#define JUST_GENERATED_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "runtime.h"

typedef struct Calculator Calculator;
typedef struct Main Main;

/* 类: Calculator */
struct Calculator {
};

int Calculator_add(Calculator* self, int a, int b);
int Calculator_multiply(Calculator* self, int a, int b);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
