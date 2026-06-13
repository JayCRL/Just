/* Just 语言编译器生成的 C 代码 */
#ifndef JUST_GENERATED_H
#define JUST_GENERATED_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "runtime.h"

typedef struct Task Task;
typedef struct Counter Counter;
typedef struct Main Main;

/* 类: Task */
struct Task {
    int id;
    int status;
};

Task* Task_Task(int taskId);
void Task_start(Task* self);
void Task_complete(Task* self);

/* 类: Counter */
struct Counter {
    int value;
};

Counter* Counter_Counter();
void Counter_increment(Counter* self);
void Counter_display(Counter* self);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
