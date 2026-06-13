/* Just 语言编译器生成的 C 代码 */
#ifndef JUST_GENERATED_H
#define JUST_GENERATED_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "runtime.h"

typedef struct User User;
typedef struct Optional Optional;
typedef struct Result Result;
typedef struct Main Main;

/* 类: User */
struct User {
    char* name;
    int age;
};

User* User_User(char* n, int a);
char* User_getName(User* self);
int User_getAge(User* self);

/* 类: Optional */
struct Optional {
    int hasValue;
    User* value;
};

Optional* Optional_Optional();
void Optional_setValue(Optional* self, User* v);
int Optional_isEmpty(Optional* self);
User* Optional_getValue(Optional* self);

/* 类: Result */
struct Result {
    int success;
    char* errorMessage;
};

Result* Result_Result(int s);
void Result_setError(Result* self, char* msg);
int Result_isSuccess(Result* self);

/* 类: Main */
struct Main {
};

void Main_main(Main* self);

#endif
