package just;

/**
 * Token 表示源代码中的一个词法单元
 */
public class Token {
    public final TokenType type;
    public final String lexeme;      // 原始文本
    public final Object literal;     // 字面量值（如果有）
    public final int line;
    public final int column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', %s, %d:%d)",
            type, lexeme, literal, line, column);
    }
}
