/*
 * Just 2.0 并发运行时库
 */

#ifndef JUST_COROUTINE_H
#define JUST_COROUTINE_H

#include <stdint.h>
#include <stdbool.h>

/* 协程状态 */
typedef enum {
    COROUTINE_READY,     // 就绪
    COROUTINE_RUNNING,   // 运行中
    COROUTINE_SUSPENDED, // 挂起
    COROUTINE_DEAD       // 已完成
} CoroutineState;

/* 协程结构 */
typedef struct Coroutine {
    int id;
    CoroutineState state;
    void* stack;
    size_t stack_size;
    void* context;
    void (*func)(void*);
    void* arg;
    void* result;
} Coroutine;

/* 调度器 */
typedef struct Scheduler {
    Coroutine** coroutines;
    int capacity;
    int count;
    int current;
    bool running;
} Scheduler;

/* 全局调度器 */
extern Scheduler* global_scheduler;

/* 协程 API */
void just_coroutine_init();
Coroutine* just_coroutine_create(void (*func)(void*), void* arg);
void just_coroutine_yield();
void just_coroutine_resume(Coroutine* co);
void* just_coroutine_await(Coroutine* co);
void just_scheduler_run();
void just_scheduler_stop();

/* Channel - 协程间通信 */
typedef struct Channel {
    void** buffer;
    int capacity;
    int size;
    int head;
    int tail;
    bool closed;
    void* send_mutex;
    void* recv_mutex;
    void* not_empty;
    void* not_full;
} Channel;

Channel* just_channel_new(int capacity);
bool just_channel_send(Channel* ch, void* value);
void* just_channel_receive(Channel* ch);
void just_channel_close(Channel* ch);
bool just_channel_is_open(Channel* ch);

/* Mutex - 互斥锁 */
typedef struct Mutex {
    void* handle;
} Mutex;

Mutex* just_mutex_new();
void just_mutex_lock(Mutex* m);
void just_mutex_unlock(Mutex* m);
void just_mutex_free(Mutex* m);

/* WaitGroup - 等待组 */
typedef struct WaitGroup {
    int counter;
    void* mutex;
    void* cond;
} WaitGroup;

WaitGroup* just_waitgroup_new();
void just_waitgroup_add(WaitGroup* wg, int delta);
void just_waitgroup_done(WaitGroup* wg);
void just_waitgroup_wait(WaitGroup* wg);
void just_waitgroup_free(WaitGroup* wg);

/* Future - 异步结果 */
typedef struct Future {
    Coroutine* coroutine;
    void* result;
    bool ready;
    void* mutex;
} Future;

Future* just_future_new(void (*func)(void*), void* arg);
void* just_future_wait(Future* f);
bool just_future_is_ready(Future* f);

#endif /* JUST_COROUTINE_H */
