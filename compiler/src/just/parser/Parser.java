package just.parser;
import just.ast.ASTNodes.*;

import just.Token;
import just.TokenType;
import just.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器 - 递归下降解析器
 * 将 Token 流转换为 AST
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {
        List<ClassDecl> classes = new ArrayList<>();

        while (!isAtEnd()) {
            classes.add(parseClassDecl());
        }

        return new Program(classes, 0, 0);
    }

    // ==================== 类声明 ====================

    private ClassDecl parseClassDecl() {
        Token classToken = consume(TokenType.CLASS, "Expected 'class'");
        Token name = consume(TokenType.IDENTIFIER, "Expected class name");

        // 解析泛型类型参数 <T> 或 <T, U>
        List<TypeParameter> typeParameters = new ArrayList<>();
        if (match(TokenType.LT)) {
            do {
                Token typeParamName = consume(TokenType.IDENTIFIER, "Expected type parameter name");
                // 暂时不处理类型约束 (where T : SomeClass)
                typeParameters.add(new TypeParameter(typeParamName.lexeme, new ArrayList<>(),
                                                    typeParamName.line, typeParamName.column));
            } while (match(TokenType.COMMA));
            consume(TokenType.GT, "Expected '>'");
        }

        String superClass = null;
        if (match(TokenType.EXTENDS)) {
            Token superName = consume(TokenType.IDENTIFIER, "Expected superclass name");
            superClass = superName.lexeme;
        }

        List<String> interfaces = new ArrayList<>();
        if (match(TokenType.IMPLEMENTS)) {
            do {
                Token iface = consume(TokenType.IDENTIFIER, "Expected interface name");
                interfaces.add(iface.lexeme);
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.LBRACE, "Expected '{'");

        List<FieldDecl> fields = new ArrayList<>();
        List<MethodDecl> methods = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            // 检查访问修饰符
            boolean isPublic = true;
            if (match(TokenType.PRIVATE)) {
                isPublic = false;
            } else if (match(TokenType.PUBLIC)) {
                isPublic = true;
            }

            // 检查是否是操作符重载
            if (check(TokenType.OPERATOR)) {
                methods.add(parseOperatorOverload(isPublic, name.lexeme));
                continue;
            }

            // 先看下一个 token，判断是构造函数还是普通成员
            Token firstToken = peek();

            // 如果是构造函数（类名后跟左括号）
            if (firstToken.type == TokenType.IDENTIFIER &&
                firstToken.lexeme.equals(name.lexeme) &&
                peekNext() != null && peekNext().type == TokenType.LPAREN) {

                // 构造函数
                advance(); // 跳过类名
                Type dummyType = new Type("void", false, firstToken.line, firstToken.column);
                methods.add(parseMethodDecl(isPublic, dummyType, firstToken.lexeme,
                                          true, false));
                continue;
            }

            // 解析类型
            Type type = parseType();
            Token memberName = consume(TokenType.IDENTIFIER, "Expected member name");

            if (check(TokenType.LPAREN)) {
                // 方法
                methods.add(parseMethodDecl(isPublic, type, memberName.lexeme,
                                          false, false));
            } else {
                // 字段
                Expr initializer = null;
                if (match(TokenType.ASSIGN)) {
                    initializer = parseExpression();
                }
                consume(TokenType.SEMICOLON, "Expected ';'");
                fields.add(new FieldDecl(isPublic, type, memberName.lexeme, initializer,
                                        memberName.line, memberName.column));
            }
        }

        consume(TokenType.RBRACE, "Expected '}'");

        return new ClassDecl(name.lexeme, superClass, interfaces, fields, methods,
                            classToken.line, classToken.column);
    }

    private MethodDecl parseMethodDecl(boolean isPublic, Type returnType,
                                       String name, boolean isConstructor,
                                       boolean isOperator) {
        int line = previous().line;
        int column = previous().column;

        consume(TokenType.LPAREN, "Expected '('");

        List<Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                Type paramType = parseType();
                Token paramName = consume(TokenType.IDENTIFIER, "Expected parameter name");
                parameters.add(new Parameter(paramType, paramName.lexeme,
                                            paramName.line, paramName.column));
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expected ')'");

        // 构造函数简化语法：Point(int x, int y);
        BlockStmt body;
        if (match(TokenType.SEMICOLON)) {
            // 自动生成赋值语句
            List<Stmt> stmts = new ArrayList<>();
            for (Parameter param : parameters) {
                // this.x = x;
                Expr target = new MemberExpr(new ThisExpr(line, column),
                                             param.name, line, column);
                Expr value = new IdentifierExpr(param.name, line, column);
                stmts.add(new ExprStmt(new AssignExpr(target, "=", value,
                                                       line, column), line, column));
            }
            body = new BlockStmt(stmts, line, column);
        } else {
            body = parseBlockStmt();
        }

        return new MethodDecl(isPublic, returnType, name, parameters, body,
                             isConstructor, isOperator, line, column);
    }

    private MethodDecl parseOperatorOverload(boolean isPublic, String className) {
        Token opToken = consume(TokenType.OPERATOR, "Expected 'operator'");

        // 解析操作符
        String operator = null;
        if (match(TokenType.PLUS)) operator = "+";
        else if (match(TokenType.MINUS)) operator = "-";
        else if (match(TokenType.STAR)) operator = "*";
        else if (match(TokenType.SLASH)) operator = "/";
        else if (match(TokenType.EQ)) operator = "==";
        else if (match(TokenType.NE)) operator = "!=";
        else if (match(TokenType.LT)) operator = "<";
        else if (match(TokenType.GT)) operator = ">";
        else if (match(TokenType.LE)) operator = "<=";
        else if (match(TokenType.GE)) operator = ">=";
        else if (match(TokenType.LBRACKET)) {
            consume(TokenType.RBRACKET, "Expected ']'");
            operator = "[]";
        } else {
            error("Invalid operator for overloading");
        }

        // 解析参数和方法体
        consume(TokenType.LPAREN, "Expected '('");
        Type paramType = parseType();
        Token paramName = consume(TokenType.IDENTIFIER, "Expected parameter name");
        consume(TokenType.RPAREN, "Expected ')'");

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(paramType, paramName.lexeme,
                                    paramName.line, paramName.column));

        BlockStmt body = parseBlockStmt();

        // 返回类型根据操作符推断
        Type returnType;
        if (operator.equals("==") || operator.equals("!=") ||
            operator.equals("<") || operator.equals(">") ||
            operator.equals("<=") || operator.equals(">=")) {
            returnType = new Type("bool", false, opToken.line, opToken.column);
        } else {
            returnType = new Type(className, false, opToken.line, opToken.column);
        }

        return new MethodDecl(isPublic, returnType, "operator" + operator,
                             parameters, body, false, true,
                             opToken.line, opToken.column);
    }

    // ==================== 类型 ====================

    private Type parseType() {
        Token typeName = advance();
        boolean isArray = false;

        if (match(TokenType.LBRACKET)) {
            consume(TokenType.RBRACKET, "Expected ']'");
            isArray = true;
        }

        return new Type(typeName.lexeme, isArray, typeName.line, typeName.column);
    }

    // ==================== 语句 ====================

    private Stmt parseStatement() {
        if (match(TokenType.IF)) return parseIfStmt();
        if (match(TokenType.WHILE)) return parseWhileStmt();
        if (match(TokenType.FOR)) return parseForStmt();
        if (match(TokenType.RETURN)) return parseReturnStmt();
        if (match(TokenType.BREAK)) {
            Token token = previous();
            consume(TokenType.SEMICOLON, "Expected ';'");
            return new BreakStmt(token.line, token.column);
        }
        if (match(TokenType.CONTINUE)) {
            Token token = previous();
            consume(TokenType.SEMICOLON, "Expected ';'");
            return new ContinueStmt(token.line, token.column);
        }
        if (match(TokenType.LBRACE)) {
            Token token = previous();
            List<Stmt> statements = new ArrayList<>();

            while (!check(TokenType.RBRACE) && !isAtEnd()) {
                statements.add(parseStatement());
            }

            consume(TokenType.RBRACE, "Expected '}'");
            return new BlockStmt(statements, token.line, token.column);
        }

        // 变量声明或表达式语句
        // 需要区分 "int x = 5;" 和 "println();"
        if (isType(peek())) {
            Token next = peekNext();
            // 如果类型后面跟着标识符，那是变量声明
            // 如果类型后面跟着 (，那是函数调用
            if (next != null && next.type == TokenType.IDENTIFIER) {
                return parseVarDeclStmt();
            }
        }

        return parseExprStmt();
    }

    private BlockStmt parseBlockStmt() {
        consume(TokenType.LBRACE, "Expected '{'");
        Token token = previous();
        List<Stmt> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        consume(TokenType.RBRACE, "Expected '}'");
        return new BlockStmt(statements, token.line, token.column);
    }

    private IfStmt parseIfStmt() {
        Token token = previous();
        consume(TokenType.LPAREN, "Expected '('");
        Expr condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')'");

        Stmt thenBranch = parseStatement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }

        return new IfStmt(condition, thenBranch, elseBranch,
                         token.line, token.column);
    }

    private WhileStmt parseWhileStmt() {
        Token token = previous();
        consume(TokenType.LPAREN, "Expected '('");
        Expr condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')'");
        Stmt body = parseStatement();

        return new WhileStmt(condition, body, token.line, token.column);
    }

    private ForStmt parseForStmt() {
        Token token = previous();
        consume(TokenType.LPAREN, "Expected '('");

        // 检查是否是增强 for 循环
        int savePoint = current;
        if (isType(peek())) {
            Type varType = parseType();
            if (check(TokenType.IDENTIFIER)) {
                Token varName = advance();
                if (match(TokenType.COLON)) {
                    // 增强 for: for (int x : arr)
                    Expr iterable = parseExpression();
                    consume(TokenType.RPAREN, "Expected ')'");
                    Stmt body = parseStatement();
                    return new ForStmt(varType, varName.lexeme, iterable, body,
                                      token.line, token.column);
                }
            }
        }

        // 传统 for 循环
        current = savePoint;

        Stmt initializer = null;
        if (!check(TokenType.SEMICOLON)) {
            if (isType(peek())) {
                initializer = parseVarDeclStmt();
            } else {
                initializer = parseExprStmt();
            }
        } else {
            consume(TokenType.SEMICOLON, "Expected ';'");
        }

        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected ';'");

        Expr increment = null;
        if (!check(TokenType.RPAREN)) {
            increment = parseExpression();
        }
        consume(TokenType.RPAREN, "Expected ')'");

        Stmt body = parseStatement();

        return new ForStmt(initializer, condition, increment, body,
                          token.line, token.column);
    }

    private ReturnStmt parseReturnStmt() {
        Token token = previous();
        Expr value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new ReturnStmt(value, token.line, token.column);
    }

    private VarDeclStmt parseVarDeclStmt() {
        Type type = parseType();
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name");

        Expr initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Expected ';'");
        return new VarDeclStmt(type, name.lexeme, initializer,
                              name.line, name.column);
    }

    private ExprStmt parseExprStmt() {
        Expr expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new ExprStmt(expr, expr.line, expr.column);
    }

    // ==================== 表达式 ====================

    private Expr parseExpression() {
        return parseAssignment();
    }

    private Expr parseAssignment() {
        Expr expr = parseLogicalOr();

        if (match(TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN)) {
            Token op = previous();
            Expr value = parseAssignment();
            return new AssignExpr(expr, op.lexeme, value, op.line, op.column);
        }

        return expr;
    }

    private Expr parseLogicalOr() {
        Expr expr = parseLogicalAnd();

        while (match(TokenType.OR)) {
            Token op = previous();
            Expr right = parseLogicalAnd();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseLogicalAnd() {
        Expr expr = parseEquality();

        while (match(TokenType.AND)) {
            Token op = previous();
            Expr right = parseEquality();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseEquality() {
        Expr expr = parseComparison();

        while (match(TokenType.EQ, TokenType.NE)) {
            Token op = previous();
            Expr right = parseComparison();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseComparison() {
        Expr expr = parseAddition();

        while (match(TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE)) {
            Token op = previous();
            Expr right = parseAddition();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseAddition() {
        Expr expr = parseMultiplication();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = previous();
            Expr right = parseMultiplication();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseMultiplication() {
        Expr expr = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token op = previous();
            Expr right = parseUnary();
            expr = new BinaryExpr(expr, op.lexeme, right, op.line, op.column);
        }

        return expr;
    }

    private Expr parseUnary() {
        if (match(TokenType.NOT, TokenType.MINUS, TokenType.INC, TokenType.DEC)) {
            Token op = previous();
            Expr expr = parseUnary();
            return new UnaryExpr(op.lexeme, expr, true, op.line, op.column);
        }

        return parsePostfix();
    }

    private Expr parsePostfix() {
        Expr expr = parsePrimary();

        while (true) {
            if (match(TokenType.DOT)) {
                Token member = consume(TokenType.IDENTIFIER, "Expected member name");
                expr = new MemberExpr(expr, member.lexeme, member.line, member.column);
            } else if (match(TokenType.LBRACKET)) {
                Expr index = parseExpression();
                consume(TokenType.RBRACKET, "Expected ']'");
                expr = new IndexExpr(expr, index, expr.line, expr.column);
            } else if (match(TokenType.LPAREN)) {
                List<Expr> args = new ArrayList<>();
                if (!check(TokenType.RPAREN)) {
                    do {
                        args.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }
                Token closeParen = consume(TokenType.RPAREN, "Expected ')'");
                expr = new CallExpr(expr, args, closeParen.line, closeParen.column);
            } else if (match(TokenType.INC, TokenType.DEC)) {
                Token op = previous();
                expr = new UnaryExpr(op.lexeme, expr, false, op.line, op.column);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr parsePrimary() {
        if (match(TokenType.TRUE)) {
            Token token = previous();
            return new LiteralExpr(true, token.line, token.column);
        }
        if (match(TokenType.FALSE)) {
            Token token = previous();
            return new LiteralExpr(false, token.line, token.column);
        }
        if (match(TokenType.NULL)) {
            Token token = previous();
            return new LiteralExpr(null, token.line, token.column);
        }

        if (match(TokenType.INT_LITERAL, TokenType.FLOAT_LITERAL, TokenType.CHAR_LITERAL)) {
            Token token = previous();
            return new LiteralExpr(token.literal, token.line, token.column);
        }

        if (match(TokenType.STRING_LITERAL)) {
            Token token = previous();
            String str = (String) token.literal;

            // 检查字符串插值
            if (str.contains("${")) {
                return parseStringInterpolation(str, token.line, token.column);
            }

            return new LiteralExpr(str, token.line, token.column);
        }

        if (match(TokenType.THIS)) {
            Token token = previous();
            return new ThisExpr(token.line, token.column);
        }

        if (match(TokenType.SUPER)) {
            Token token = previous();
            consume(TokenType.DOT, "Expected '.' after 'super'");
            Token member = consume(TokenType.IDENTIFIER, "Expected member name");
            return new SuperExpr(member.lexeme, token.line, token.column);
        }

        if (match(TokenType.NEW)) {
            return parseNewExpr();
        }

        if (match(TokenType.IDENTIFIER)) {
            Token token = previous();
            return new IdentifierExpr(token.lexeme, token.line, token.column);
        }

        if (match(TokenType.LPAREN)) {
            Token token = previous();
            Expr expr = parseExpression();
            consume(TokenType.RPAREN, "Expected ')'");
            return expr;
        }

        if (match(TokenType.LBRACKET)) {
            return parseArrayLiteral();
        }

        error("Expected expression");
        return null;
    }

    private Expr parseNewExpr() {
        Token token = previous();
        Type type = parseType();

        if (match(TokenType.LBRACKET)) {
            Expr size = parseExpression();
            consume(TokenType.RBRACKET, "Expected ']'");
            return new NewExpr(type, size, token.line, token.column);
        }

        consume(TokenType.LPAREN, "Expected '('");
        List<Expr> args = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                args.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expected ')'");

        return new NewExpr(type, args, token.line, token.column);
    }

    private Expr parseArrayLiteral() {
        Token token = previous();
        List<Expr> elements = new ArrayList<>();

        if (!check(TokenType.RBRACKET)) {
            do {
                elements.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RBRACKET, "Expected ']'");
        return new ArrayLiteralExpr(elements, token.line, token.column);
    }

    private Expr parseStringInterpolation(String str, int line, int column) {
        List<Object> parts = new ArrayList<>();
        int pos = 0;

        while (pos < str.length()) {
            int start = str.indexOf("${", pos);
            if (start == -1) {
                parts.add(str.substring(pos));
                break;
            }

            if (start > pos) {
                parts.add(str.substring(pos, start));
            }

            int end = str.indexOf("}", start);
            if (end == -1) {
                error("Unclosed string interpolation");
                break;
            }

            String exprStr = str.substring(start + 2, end);
            // 简单解析：只支持标识符和成员访问
            parts.add(new IdentifierExpr(exprStr.trim(), line, column));

            pos = end + 1;
        }

        return new StringInterpolationExpr(parts, line, column);
    }

    // ==================== 工具方法 ====================

    private boolean isType(Token token) {
        return token.type == TokenType.INT || token.type == TokenType.LONG ||
               token.type == TokenType.FLOAT || token.type == TokenType.DOUBLE ||
               token.type == TokenType.BOOL || token.type == TokenType.CHAR ||
               token.type == TokenType.STRING || token.type == TokenType.VOID ||
               token.type == TokenType.IDENTIFIER;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        if (current + 1 >= tokens.size()) return null;
        return tokens.get(current + 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        error(message);
        return null;
    }

    private void error(String message) {
        Token token = peek();
        throw new RuntimeException("[Parser Error] Line " + token.line +
                                  ":" + token.column + " - " + message);
    }
}
