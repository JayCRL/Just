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
    int value;
};

Calculator* Calculator_Calculator(int v);
int Calculator_add(Calculator* self, int x);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
