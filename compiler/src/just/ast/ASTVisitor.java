package just.ast;

import just.ast.ASTNodes.*;

/**
 * AST 访问者接口（用于代码生成、语义分析等）
 */
public interface ASTVisitor<T> {
    // 程序结构
    T visitProgram(Program node);
    T visitClassDecl(ClassDecl node);
    T visitMethodDecl(MethodDecl node);
    T visitFieldDecl(FieldDecl node);
    T visitParameter(Parameter node);

    // 语句
    T visitBlockStmt(BlockStmt node);
    T visitIfStmt(IfStmt node);
    T visitWhileStmt(WhileStmt node);
    T visitForStmt(ForStmt node);
    T visitReturnStmt(ReturnStmt node);
    T visitBreakStmt(BreakStmt node);
    T visitContinueStmt(ContinueStmt node);
    T visitExprStmt(ExprStmt node);
    T visitVarDeclStmt(VarDeclStmt node);

    // 表达式
    T visitBinaryExpr(BinaryExpr node);
    T visitUnaryExpr(UnaryExpr node);
    T visitCallExpr(CallExpr node);
    T visitMemberExpr(MemberExpr node);
    T visitIndexExpr(IndexExpr node);
    T visitNewExpr(NewExpr node);
    T visitLiteralExpr(LiteralExpr node);
    T visitIdentifierExpr(IdentifierExpr node);
    T visitAssignExpr(AssignExpr node);
    T visitThisExpr(ThisExpr node);
    T visitSuperExpr(SuperExpr node);
    T visitArrayLiteralExpr(ArrayLiteralExpr node);
    T visitStringInterpolationExpr(StringInterpolationExpr node);

    // 类型
    T visitType(Type node);
}
