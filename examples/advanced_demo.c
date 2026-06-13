/* Just 语言编译器生成的 C 代码 */
#include "just_generated.h"

/* User.User */
User* User_User(char* n, int a) {
    User* self = (User*)just_alloc(sizeof(User));
    self->name = n;
    self->age = a;
    return self;
}

/* User.getName */
char* User_getName(User* self) {
    return self->name;
}

/* User.getAge */
int User_getAge(User* self) {
    return self->age;
}

/* Optional.Optional */
Optional* Optional_Optional() {
    Optional* self = (Optional*)just_alloc(sizeof(Optional));
    self->hasValue = 0;
    return self;
}

/* Optional.setValue */
void Optional_setValue(Optional* self, User* v) {
    self->value = v;
    self->hasValue = 1;
}

/* Optional.isEmpty */
int Optional_isEmpty(Optional* self) {
    if ((self->hasValue == 0)) {
        return 1;
    } else {
        return 0;
    }
}

/* Optional.getValue */
User* Optional_getValue(Optional* self) {
    return self->value;
}

/* Result.Result */
Result* Result_Result(int s) {
    Result* self = (Result*)just_alloc(sizeof(Result));
    self->success = s;
    return self;
}

/* Result.setError */
void Result_setError(Result* self, char* msg) {
    self->errorMessage = msg;
    self->success = 0;
}

/* Result.isSuccess */
int Result_isSuccess(Result* self) {
    return self->success;
}

/* Main.main */
void Main_main(Main* self) {
    just_println_string("=== Just 2.0 - Advanced Features Demo ===");
    just_println_string("");
    just_println_string("1. Optional pattern (nullable types):");
    Optional* maybeUser = Optional_Optional();
    if ((Optional_isEmpty(maybeUser) == 1)) {
        just_println_string("User is not present");
    } else {
        just_println_string("User is present");
    }
    User* user = User_User("Alice", 25);
    Optional_setValue(maybeUser, user);
    if ((Optional_isEmpty(maybeUser) == 0)) {
        just_println_string("User is now present!");
    } else {
        just_println_string("Still empty");
    }
    just_println_string("");
    just_println_string("2. Result pattern (error handling):");
    Result* result = Result_Result(1);
    if ((Result_isSuccess(result) == 1)) {
        just_println_string("Operation succeeded");
    } else {
        just_println_string("Operation failed");
    }
    Result_setError(result, "Network timeout");
    if ((Result_isSuccess(result) == 0)) {
        just_println_string("Operation failed with error");
    } else {
        just_println_string("Still success");
    }
    just_println_string("");
    just_println_string("=== Advanced patterns demonstrated! ===");
}

/* 程序入口 */
int main(int argc, char** argv) {
    just_runtime_init();

    Main* main_obj = Main_Main();
    Main_main(main_obj);

    return 0;
}
