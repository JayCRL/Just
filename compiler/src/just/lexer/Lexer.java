package just.lexer;

import just.Token;
import just.TokenType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词法分析器 - 将源代码转换为 Token 流
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;      // 当前词素的起始位置
    private int current = 0;    // 当前扫描位置
    private int line = 1;
    private int column = 1;

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("extends", TokenType.EXTENDS);
        keywords.put("interface", TokenType.INTERFACE);
        keywords.put("implements", TokenType.IMPLEMENTS);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("void", TokenType.VOID);
        keywords.put("int", TokenType.INT);
        keywords.put("long", TokenType.LONG);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("double", TokenType.DOUBLE);
        keywords.put("bool", TokenType.BOOL);
        keywords.put("char", TokenType.CHAR);
        keywords.put("string", TokenType.STRING);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("new", TokenType.NEW);
        keywords.put("this", TokenType.THIS);
        keywords.put("super", TokenType.SUPER);
        keywords.put("null", TokenType.NULL);
        keywords.put("const", TokenType.CONST);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("operator", TokenType.OPERATOR);

        // Just 2.0 新增关键字
        keywords.put("async", TokenType.ASYNC);
        keywords.put("await", TokenType.AWAIT);
        keywords.put("channel", TokenType.CHANNEL);
        keywords.put("future", TokenType.FUTURE);
        keywords.put("mutex", TokenType.MUTEX);
        keywords.put("synchronized", TokenType.SYNCHRONIZED);
        keywords.put("where", TokenType.WHERE);
        keywords.put("extension", TokenType.EXTENSION);
        keywords.put("struct", TokenType.STRUCT);
        keywords.put("enum", TokenType.ENUM);
        keywords.put("unsafe", TokenType.UNSAFE);
        keywords.put("comptime", TokenType.COMPTIME);
        keywords.put("case", TokenType.CASE);
        keywords.put("match", TokenType.MATCH);
        keywords.put("get", TokenType.GET);
        keywords.put("set", TokenType.SET);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LPAREN); break;
            case ')': addToken(TokenType.RPAREN); break;
            case '[': addToken(TokenType.LBRACKET); break;
            case ']': addToken(TokenType.RBRACKET); break;
            case '{': addToken(TokenType.LBRACE); break;
            case '}': addToken(TokenType.RBRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.': addToken(TokenType.DOT); break;
            case '?': addToken(TokenType.QUESTION); break;
            case ':': addToken(TokenType.COLON); break;

            case '+':
                if (match('+')) addToken(TokenType.INC);
                else if (match('=')) addToken(TokenType.PLUS_ASSIGN);
                else addToken(TokenType.PLUS);
                break;
            case '-':
                if (match('-')) addToken(TokenType.DEC);
                else if (match('=')) addToken(TokenType.MINUS_ASSIGN);
                else if (match('>')) addToken(TokenType.ARROW);
                else addToken(TokenType.MINUS);
                break;
            case '*': addToken(TokenType.STAR); break;
            case '%': addToken(TokenType.PERCENT); break;

            case '!':
                if (match('=')) addToken(TokenType.NE);
                else addToken(TokenType.NOT_NULL);
                break;
            case '=':
                if (match('=')) addToken(TokenType.EQ);
                else if (match('>')) addToken(TokenType.DOUBLE_ARROW);
                else addToken(TokenType.ASSIGN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LE : TokenType.LT);
                break;
            case '>':
                addToken(match('=') ? TokenType.GE : TokenType.GT);
                break;

            case '/':
                if (match('/')) {
                    // 单行注释
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // 多行注释
                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case '&':
                if (match('&')) addToken(TokenType.AND);
                else error("Unexpected character: &");
                break;
            case '|':
                if (match('|')) addToken(TokenType.OR);
                else error("Unexpected character: |");
                break;

            case ' ':
            case '\r':
            case '\t':
                // 忽略空白
                break;

            case '\n':
                line++;
                column = 1;
                break;

            case '"': string(); break;
            case '\'': character(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    error("Unexpected character: " + c);
                }
                break;
        }
    }

    private void blockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // *
                advance(); // /
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // 查找小数点
        boolean isFloat = false;
        if (peek() == '.' && isDigit(peekNext())) {
            isFloat = true;
            advance(); // 消费 '.'
            while (isDigit(peek())) advance();
        }

        String text = source.substring(start, current);
        if (isFloat) {
            addToken(TokenType.FLOAT_LITERAL, Double.parseDouble(text));
        } else {
            addToken(TokenType.INT_LITERAL, Long.parseLong(text));
        }
    }

    private void string() {
        StringBuilder sb = new StringBuilder();

        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance();
                if (!isAtEnd()) {
                    char escaped = advance();
                    switch (escaped) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case '\\': sb.append('\\'); break;
                        case '"': sb.append('"'); break;
                        case '$': sb.append('$'); break;
                        default: sb.append(escaped); break;
                    }
                }
            } else {
                sb.append(advance());
            }
        }

        if (isAtEnd()) {
            error("Unterminated string");
            return;
        }

        advance(); // 消费结束的 "

        addToken(TokenType.STRING_LITERAL, sb.toString());
    }

    private void character() {
        if (isAtEnd()) {
            error("Unterminated character");
            return;
        }

        char value;
        if (peek() == '\\') {
            advance();
            if (isAtEnd()) {
                error("Unterminated character");
                return;
            }
            char escaped = advance();
            switch (escaped) {
                case 'n': value = '\n'; break;
                case 't': value = '\t'; break;
                case 'r': value = '\r'; break;
                case '\\': value = '\\'; break;
                case '\'': value = '\''; break;
                default: value = escaped; break;
            }
        } else {
            value = advance();
        }

        if (peek() != '\'') {
            error("Unterminated character");
            return;
        }
        advance(); // 消费结束的 '

        addToken(TokenType.CHAR_LITERAL, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        column++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        column++;
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, column - text.length()));
    }

    private void error(String message) {
        System.err.println("[Lexer Error] Line " + line + ":" + column + " - " + message);
    }
}
