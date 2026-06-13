/* Just 语言编译器生成的 C 代码 */
#ifndef JUST_GENERATED_H
#define JUST_GENERATED_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "runtime.h"

typedef struct Point Point;
typedef struct Status Status;
typedef struct Counter Counter;
typedef struct OptionalInt OptionalInt;
typedef struct Main Main;

/* 类: Point */
struct Point {
    int x;
    int y;
};

Point* Point_Point(int px, int py);
int Point_getX(Point* self);
int Point_getY(Point* self);
void Point_move(Point* self, int dx, int dy);
int Point_distanceFromOrigin(Point* self);

/* 类: Status */
struct Status {
    int code;
};

Status* Status_Status(int c);
bool Status_isSuccess(Status* self);
bool Status_isError(Status* self);

/* 类: Counter */
struct Counter {
    int count;
};

Counter* Counter_Counter();
void Counter_increment(Counter* self);
void Counter_decrement(Counter* self);
void Counter_reset(Counter* self);
int Counter_getValue(Counter* self);
bool Counter_isZero(Counter* self);
bool Counter_isPositive(Counter* self);

/* 类: OptionalInt */
struct OptionalInt {
    int hasValue;
    int value;
};

OptionalInt* OptionalInt_OptionalInt();
void OptionalInt_setValue(OptionalInt* self, int v);
bool OptionalInt_isEmpty(OptionalInt* self);
int OptionalInt_getOrDefault(OptionalInt* self, int defaultValue);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
