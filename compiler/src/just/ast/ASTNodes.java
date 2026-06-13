package just.ast;

import java.util.List;

/**
 * Just AST 节点定义
 * 将所有 AST 类作为静态内部类放在一起
 */
public class ASTNodes {

    // ==================== 声明节点 ====================

    public static class Program extends ASTNode {
        public final List<ClassDecl> classes;

        public Program(List<ClassDecl> classes, int line, int column) {
            super(line, column);
            this.classes = classes;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitProgram(this);
        }
    }

    public static class ClassDecl extends ASTNode {
        public final String name;
        public final String superClass;
        public final List<String> interfaces;
        public final List<FieldDecl> fields;
        public final List<MethodDecl> methods;

        public ClassDecl(String name, String superClass, List<String> interfaces,
                         List<FieldDecl> fields, List<MethodDecl> methods,
                         int line, int column) {
            super(line, column);
            this.name = name;
            this.superClass = superClass;
            this.interfaces = interfaces;
            this.fields = fields;
            this.methods = methods;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitClassDecl(this);
        }
    }

    public static class FieldDecl extends ASTNode {
        public final boolean isPublic;
        public final Type type;
        public final String name;
        public final Expr initializer;

        public FieldDecl(boolean isPublic, Type type, String name, Expr initializer,
                         int line, int column) {
            super(line, column);
            this.isPublic = isPublic;
            this.type = type;
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitFieldDecl(this);
        }
    }

    public static class MethodDecl extends ASTNode {
        public final boolean isPublic;
        public final Type returnType;
        public final String name;
        public final List<Parameter> parameters;
        public final BlockStmt body;
        public final boolean isConstructor;
        public final boolean isOperator;

        public MethodDecl(boolean isPublic, Type returnType, String name,
                          List<Parameter> parameters, BlockStmt body,
                          boolean isConstructor, boolean isOperator,
                          int line, int column) {
            super(line, column);
            this.isPublic = isPublic;
            this.returnType = returnType;
            this.name = name;
            this.parameters = parameters;
            this.body = body;
            this.isConstructor = isConstructor;
            this.isOperator = isOperator;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitMethodDecl(this);
        }
    }

    public static class Parameter extends ASTNode {
        public final Type type;
        public final String name;

        public Parameter(Type type, String name, int line, int column) {
            super(line, column);
            this.type = type;
            this.name = name;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitParameter(this);
        }
    }

    public static class Type extends ASTNode {
        public final String name;
        public final boolean isArray;

        public Type(String name, boolean isArray, int line, int column) {
            super(line, column);
            this.name = name;
            this.isArray = isArray;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitType(this);
        }

        @Override
        public String toString() {
            return name + (isArray ? "[]" : "");
        }
    }

    // ==================== 表达式节点 ====================

    public static abstract class Expr extends ASTNode {
        public Expr(int line, int column) {
            super(line, column);
        }
    }

    public static class BinaryExpr extends Expr {
        public final Expr left;
        public final String operator;
        public final Expr right;

        public BinaryExpr(Expr left, String operator, Expr right,
                          int line, int column) {
            super(line, column);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class UnaryExpr extends Expr {
        public final String operator;
        public final Expr operand;
        public final boolean isPrefix;

        public UnaryExpr(String operator, Expr operand, boolean isPrefix,
                         int line, int column) {
            super(line, column);
            this.operator = operator;
            this.operand = operand;
            this.isPrefix = isPrefix;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class CallExpr extends Expr {
        public final Expr callee;
        public final List<Expr> arguments;

        public CallExpr(Expr callee, List<Expr> arguments, int line, int column) {
            super(line, column);
            this.callee = callee;
            this.arguments = arguments;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    public static class MemberExpr extends Expr {
        public final Expr object;
        public final String member;

        public MemberExpr(Expr object, String member, int line, int column) {
            super(line, column);
            this.object = object;
            this.member = member;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitMemberExpr(this);
        }
    }

    public static class IndexExpr extends Expr {
        public final Expr array;
        public final Expr index;

        public IndexExpr(Expr array, Expr index, int line, int column) {
            super(line, column);
            this.array = array;
            this.index = index;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIndexExpr(this);
        }
    }

    public static class NewExpr extends Expr {
        public final Type type;
        public final List<Expr> arguments;
        public final Expr arraySize;

        public NewExpr(Type type, List<Expr> arguments, int line, int column) {
            super(line, column);
            this.type = type;
            this.arguments = arguments;
            this.arraySize = null;
        }

        public NewExpr(Type type, Expr arraySize, int line, int column) {
            super(line, column);
            this.type = type;
            this.arguments = null;
            this.arraySize = arraySize;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitNewExpr(this);
        }
    }

    public static class LiteralExpr extends Expr {
        public final Object value;

        public LiteralExpr(Object value, int line, int column) {
            super(line, column);
            this.value = value;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class IdentifierExpr extends Expr {
        public final String name;

        public IdentifierExpr(String name, int line, int column) {
            super(line, column);
            this.name = name;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIdentifierExpr(this);
        }
    }

    public static class AssignExpr extends Expr {
        public final Expr target;
        public final String operator;
        public final Expr value;

        public AssignExpr(Expr target, String operator, Expr value,
                          int line, int column) {
            super(line, column);
            this.target = target;
            this.operator = operator;
            this.value = value;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    public static class ThisExpr extends Expr {
        public ThisExpr(int line, int column) {
            super(line, column);
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitThisExpr(this);
        }
    }

    public static class SuperExpr extends Expr {
        public final String member;

        public SuperExpr(String member, int line, int column) {
            super(line, column);
            this.member = member;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitSuperExpr(this);
        }
    }

    public static class ArrayLiteralExpr extends Expr {
        public final List<Expr> elements;

        public ArrayLiteralExpr(List<Expr> elements, int line, int column) {
            super(line, column);
            this.elements = elements;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitArrayLiteralExpr(this);
        }
    }

    public static class StringInterpolationExpr extends Expr {
        public final List<Object> parts;

        public StringInterpolationExpr(List<Object> parts, int line, int column) {
            super(line, column);
            this.parts = parts;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitStringInterpolationExpr(this);
        }
    }

    // ==================== 语句节点 ====================

    public static abstract class Stmt extends ASTNode {
        public Stmt(int line, int column) {
            super(line, column);
        }
    }

    public static class BlockStmt extends Stmt {
        public final List<Stmt> statements;

        public BlockStmt(List<Stmt> statements, int line, int column) {
            super(line, column);
            this.statements = statements;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class IfStmt extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch,
                      int line, int column) {
            super(line, column);
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class WhileStmt extends Stmt {
        public final Expr condition;
        public final Stmt body;

        public WhileStmt(Expr condition, Stmt body, int line, int column) {
            super(line, column);
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class ForStmt extends Stmt {
        public final Stmt initializer;
        public final Expr condition;
        public final Expr increment;
        public final Stmt body;
        public final boolean isEnhanced;
        public final Type varType;
        public final String varName;
        public final Expr iterable;

        public ForStmt(Stmt initializer, Expr condition, Expr increment,
                       Stmt body, int line, int column) {
            super(line, column);
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
            this.isEnhanced = false;
            this.varType = null;
            this.varName = null;
            this.iterable = null;
        }

        public ForStmt(Type varType, String varName, Expr iterable,
                       Stmt body, int line, int column) {
            super(line, column);
            this.varType = varType;
            this.varName = varName;
            this.iterable = iterable;
            this.body = body;
            this.isEnhanced = true;
            this.initializer = null;
            this.condition = null;
            this.increment = null;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }

    public static class ReturnStmt extends Stmt {
        public final Expr value;

        public ReturnStmt(Expr value, int line, int column) {
            super(line, column);
            this.value = value;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    public static class BreakStmt extends Stmt {
        public BreakStmt(int line, int column) {
            super(line, column);
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBreakStmt(this);
        }
    }

    public static class ContinueStmt extends Stmt {
        public ContinueStmt(int line, int column) {
            super(line, column);
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitContinueStmt(this);
        }
    }

    public static class ExprStmt extends Stmt {
        public final Expr expression;

        public ExprStmt(Expr expression, int line, int column) {
            super(line, column);
            this.expression = expression;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitExprStmt(this);
        }
    }

    public static class VarDeclStmt extends Stmt {
        public final Type type;
        public final String name;
        public final Expr initializer;

        public VarDeclStmt(Type type, String name, Expr initializer,
                           int line, int column) {
            super(line, column);
            this.type = type;
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVarDeclStmt(this);
        }
    }
}
