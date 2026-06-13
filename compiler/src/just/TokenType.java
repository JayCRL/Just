package just;

/**
 * Just 语言的所有 Token 类型
 */
public enum TokenType {
    // 关键字
    CLASS, EXTENDS, INTERFACE, IMPLEMENTS,
    PUBLIC, PRIVATE,
    VOID, INT, LONG, FLOAT, DOUBLE, BOOL, CHAR, STRING,
    IF, ELSE, FOR, WHILE, DO, BREAK, CONTINUE, RETURN,
    NEW, THIS, SUPER, NULL,
    CONST, TRUE, FALSE,
    OPERATOR,

    // Just 2.0 新增关键字
    ASYNC, AWAIT, CHANNEL, FUTURE, MUTEX, SYNCHRONIZED,  // 并发
    WHERE, EXTENSION, STRUCT, ENUM,                       // 类型系统
    UNSAFE, COMPTIME,                                     // 高级特性
    CASE, MATCH,                                          // 模式匹配
    GET, SET,                                             // 属性

    // 标识符和字面量
    IDENTIFIER,
    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHAR_LITERAL,

    // 运算符
    PLUS, MINUS, STAR, SLASH, PERCENT,          // + - * / %
    EQ, NE, LT, GT, LE, GE,                     // == != < > <= >=
    AND, OR, NOT,                               // && || !
    ASSIGN,                                     // =
    PLUS_ASSIGN, MINUS_ASSIGN,                  // += -=
    INC, DEC,                                   // ++ --
    DOT, ARROW, DOUBLE_ARROW,                   // . -> =>
    QUESTION, COLON, DOUBLE_COLON,              // ? : ::
    DOUBLE_QUESTION,                            // ?? (null coalescing)
    SAFE_ACCESS,                                // ?. (safe navigation)
    NOT_NULL,                                   // ! (force unwrap)

    // 分隔符
    LPAREN, RPAREN,                             // ( )
    LBRACKET, RBRACKET,                         // [ ]
    LBRACE, RBRACE,                             // { }
    SEMICOLON, COMMA,                           // ; ,

    // 特殊
    EOF,
    NEWLINE
}
