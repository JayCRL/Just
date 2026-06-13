/* Just 语言编译器生成的 C 代码 */
#ifndef JUST_GENERATED_H
#define JUST_GENERATED_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "runtime.h"

typedef struct TrafficLight TrafficLight;
typedef struct OrderStatus OrderStatus;
typedef struct Main Main;

/* 类: TrafficLight */
struct TrafficLight {
    int state;
};

TrafficLight* TrafficLight_TrafficLight(int s);
void TrafficLight_setState(TrafficLight* self, int s);
void TrafficLight_setRed(TrafficLight* self);
void TrafficLight_setYellow(TrafficLight* self);
void TrafficLight_setGreen(TrafficLight* self);
bool TrafficLight_isRed(TrafficLight* self);
bool TrafficLight_isYellow(TrafficLight* self);
bool TrafficLight_isGreen(TrafficLight* self);
void TrafficLight_display(TrafficLight* self);

/* 类: OrderStatus */
struct OrderStatus {
    int code;
};

OrderStatus* OrderStatus_OrderStatus();
void OrderStatus_setPending(OrderStatus* self);
void OrderStatus_setProcessing(OrderStatus* self);
void OrderStatus_setShipped(OrderStatus* self);
void OrderStatus_setDelivered(OrderStatus* self);
bool OrderStatus_isPending(OrderStatus* self);
bool OrderStatus_isDelivered(OrderStatus* self);
void OrderStatus_display(OrderStatus* self);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
