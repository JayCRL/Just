/*
 * Just 2.0 并发运行时库实现
 */

#include "coroutine.h"
#include "runtime.h"
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <ucontext.h>

#define COROUTINE_STACK_SIZE (64 * 1024)  // 64KB 栈
#define MAX_COROUTINES 10000

/* 全局调度器 */
Scheduler* global_scheduler = NULL;

/* ==================== 协程实现 ==================== */

void just_coroutine_init() {
    if (global_scheduler != NULL) return;

    global_scheduler = (Scheduler*)just_alloc(sizeof(Scheduler));
    global_scheduler->capacity = 100;
    global_scheduler->count = 0;
    global_scheduler->current = -1;
    global_scheduler->running = false;
    global_scheduler->coroutines = (Coroutine**)just_alloc(
        sizeof(Coroutine*) * global_scheduler->capacity);
}

Coroutine* just_coroutine_create(void (*func)(void*), void* arg) {
    if (global_scheduler == NULL) {
        just_coroutine_init();
    }

    Coroutine* co = (Coroutine*)just_alloc(sizeof(Coroutine));
    co->id = global_scheduler->count;
    co->state = COROUTINE_READY;
    co->stack = just_alloc(COROUTINE_STACK_SIZE);
    co->stack_size = COROUTINE_STACK_SIZE;
    co->func = func;
    co->arg = arg;
    co->result = NULL;

    // 初始化上下文
    ucontext_t* ctx = (ucontext_t*)just_alloc(sizeof(ucontext_t));
    getcontext(ctx);
    ctx->uc_stack.ss_sp = co->stack;
    ctx->uc_stack.ss_size = co->stack_size;
    ctx->uc_link = NULL;
    co->context = ctx;

    // 添加到调度器
    if (global_scheduler->count >= global_scheduler->capacity) {
        global_scheduler->capacity *= 2;
        global_scheduler->coroutines = (Coroutine**)realloc(
            global_scheduler->coroutines,
            sizeof(Coroutine*) * global_scheduler->capacity);
    }
    global_scheduler->coroutines[global_scheduler->count++] = co;

    return co;
}

void just_coroutine_yield() {
    // 简化实现：直接返回
    // 完整实现需要保存上下文并切换到调度器
}

void just_coroutine_resume(Coroutine* co) {
    if (co->state == COROUTINE_DEAD) return;

    co->state = COROUTINE_RUNNING;

    // 执行协程函数
    if (co->func) {
        co->func(co->arg);
    }

    co->state = COROUTINE_DEAD;
}

void* just_coroutine_await(Coroutine* co) {
    while (co->state != COROUTINE_DEAD) {
        just_coroutine_resume(co);
    }
    return co->result;
}

void just_scheduler_run() {
    if (global_scheduler == NULL) return;

    global_scheduler->running = true;

    // 简单的循环调度
    for (int i = 0; i < global_scheduler->count; i++) {
        Coroutine* co = global_scheduler->coroutines[i];
        if (co->state == COROUTINE_READY || co->state == COROUTINE_SUSPENDED) {
            just_coroutine_resume(co);
        }
    }

    global_scheduler->running = false;
}

void just_scheduler_stop() {
    if (global_scheduler) {
        global_scheduler->running = false;
    }
}

/* ==================== Channel 实现 ==================== */

Channel* just_channel_new(int capacity) {
    Channel* ch = (Channel*)just_alloc(sizeof(Channel));
    ch->capacity = capacity;
    ch->size = 0;
    ch->head = 0;
    ch->tail = 0;
    ch->closed = false;
    ch->buffer = (void**)just_alloc(sizeof(void*) * capacity);

    ch->send_mutex = just_alloc(sizeof(pthread_mutex_t));
    ch->recv_mutex = just_alloc(sizeof(pthread_mutex_t));
    ch->not_empty = just_alloc(sizeof(pthread_cond_t));
    ch->not_full = just_alloc(sizeof(pthread_cond_t));

    pthread_mutex_init((pthread_mutex_t*)ch->send_mutex, NULL);
    pthread_mutex_init((pthread_mutex_t*)ch->recv_mutex, NULL);
    pthread_cond_init((pthread_cond_t*)ch->not_empty, NULL);
    pthread_cond_init((pthread_cond_t*)ch->not_full, NULL);

    return ch;
}

bool just_channel_send(Channel* ch, void* value) {
    if (ch->closed) return false;

    pthread_mutex_t* mutex = (pthread_mutex_t*)ch->send_mutex;
    pthread_cond_t* not_full = (pthread_cond_t*)ch->not_full;
    pthread_cond_t* not_empty = (pthread_cond_t*)ch->not_empty;

    pthread_mutex_lock(mutex);

    // 等待有空间
    while (ch->size >= ch->capacity && !ch->closed) {
        pthread_cond_wait(not_full, mutex);
    }

    if (ch->closed) {
        pthread_mutex_unlock(mutex);
        return false;
    }

    // 写入数据
    ch->buffer[ch->tail] = value;
    ch->tail = (ch->tail + 1) % ch->capacity;
    ch->size++;

    // 通知消费者
    pthread_cond_signal(not_empty);
    pthread_mutex_unlock(mutex);

    return true;
}

void* just_channel_receive(Channel* ch) {
    pthread_mutex_t* mutex = (pthread_mutex_t*)ch->recv_mutex;
    pthread_cond_t* not_empty = (pthread_cond_t*)ch->not_empty;
    pthread_cond_t* not_full = (pthread_cond_t*)ch->not_full;

    pthread_mutex_lock(mutex);

    // 等待有数据
    while (ch->size == 0 && !ch->closed) {
        pthread_cond_wait(not_empty, mutex);
    }

    if (ch->size == 0 && ch->closed) {
        pthread_mutex_unlock(mutex);
        return NULL;
    }

    // 读取数据
    void* value = ch->buffer[ch->head];
    ch->head = (ch->head + 1) % ch->capacity;
    ch->size--;

    // 通知生产者
    pthread_cond_signal(not_full);
    pthread_mutex_unlock(mutex);

    return value;
}

void just_channel_close(Channel* ch) {
    pthread_mutex_t* send_mutex = (pthread_mutex_t*)ch->send_mutex;

    pthread_mutex_lock(send_mutex);
    ch->closed = true;

    // 唤醒所有等待的线程
    pthread_cond_broadcast((pthread_cond_t*)ch->not_empty);
    pthread_cond_broadcast((pthread_cond_t*)ch->not_full);
    pthread_mutex_unlock(send_mutex);
}

bool just_channel_is_open(Channel* ch) {
    return !ch->closed;
}

/* ==================== Mutex 实现 ==================== */

Mutex* just_mutex_new() {
    Mutex* m = (Mutex*)just_alloc(sizeof(Mutex));
    m->handle = just_alloc(sizeof(pthread_mutex_t));
    pthread_mutex_init((pthread_mutex_t*)m->handle, NULL);
    return m;
}

void just_mutex_lock(Mutex* m) {
    pthread_mutex_lock((pthread_mutex_t*)m->handle);
}

void just_mutex_unlock(Mutex* m) {
    pthread_mutex_unlock((pthread_mutex_t*)m->handle);
}

void just_mutex_free(Mutex* m) {
    pthread_mutex_destroy((pthread_mutex_t*)m->handle);
}

/* ==================== WaitGroup 实现 ==================== */

WaitGroup* just_waitgroup_new() {
    WaitGroup* wg = (WaitGroup*)just_alloc(sizeof(WaitGroup));
    wg->counter = 0;
    wg->mutex = just_alloc(sizeof(pthread_mutex_t));
    wg->cond = just_alloc(sizeof(pthread_cond_t));

    pthread_mutex_init((pthread_mutex_t*)wg->mutex, NULL);
    pthread_cond_init((pthread_cond_t*)wg->cond, NULL);

    return wg;
}

void just_waitgroup_add(WaitGroup* wg, int delta) {
    pthread_mutex_lock((pthread_mutex_t*)wg->mutex);
    wg->counter += delta;
    pthread_mutex_unlock((pthread_mutex_t*)wg->mutex);
}

void just_waitgroup_done(WaitGroup* wg) {
    pthread_mutex_lock((pthread_mutex_t*)wg->mutex);
    wg->counter--;
    if (wg->counter == 0) {
        pthread_cond_broadcast((pthread_cond_t*)wg->cond);
    }
    pthread_mutex_unlock((pthread_mutex_t*)wg->mutex);
}

void just_waitgroup_wait(WaitGroup* wg) {
    pthread_mutex_lock((pthread_mutex_t*)wg->mutex);
    while (wg->counter > 0) {
        pthread_cond_wait((pthread_cond_t*)wg->cond,
                         (pthread_mutex_t*)wg->mutex);
    }
    pthread_mutex_unlock((pthread_mutex_t*)wg->mutex);
}

void just_waitgroup_free(WaitGroup* wg) {
    pthread_mutex_destroy((pthread_mutex_t*)wg->mutex);
    pthread_cond_destroy((pthread_cond_t*)wg->cond);
}

/* ==================== Future 实现 ==================== */

Future* just_future_new(void (*func)(void*), void* arg) {
    Future* f = (Future*)just_alloc(sizeof(Future));
    f->coroutine = just_coroutine_create(func, arg);
    f->result = NULL;
    f->ready = false;
    f->mutex = just_alloc(sizeof(pthread_mutex_t));
    pthread_mutex_init((pthread_mutex_t*)f->mutex, NULL);

    return f;
}

void* just_future_wait(Future* f) {
    if (!f->ready) {
        f->result = just_coroutine_await(f->coroutine);
        f->ready = true;
    }
    return f->result;
}

bool just_future_is_ready(Future* f) {
    return f->ready;
}
