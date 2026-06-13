package just.codegen;
import just.ast.ASTNodes.*;

import just.ast.*;
import just.semantic.*;
import java.io.*;
import java.util.*;

/**
 * C 代码生成器 - 将 AST 转换为 C 代码
 */
public class CCodeGenerator implements ASTVisitor<Void> {
    private final Map<String, ClassInfo> classes;
    private final PrintWriter header;  // .h 文件
    private final PrintWriter source;  // .c 文件
    private ClassInfo currentClass;
    private ClassInfo.MethodInfo currentMethod;
    private int indentLevel = 0;

    // 追踪局部变量的类型
    private Map<String, String> localVariableTypes = new HashMap<>();

    public CCodeGenerator(Map<String, ClassInfo> classes,
                          String outputPath) throws IOException {
        this.classes = classes;
        this.header = new PrintWriter(new FileWriter(outputPath + ".h"));
        this.source = new PrintWriter(new FileWriter(outputPath + ".c"));
    }

    public void generate(Program program) {
        // 生成头文件
        generateHeader(program);

        // 生成实现文件
        generateSource(program);

        header.close();
        source.close();

        System.out.println("C 代码生成成功！");
        System.out.println("  头文件: " + header);
        System.out.println("  源文件: " + source);
    }

    private void generateHeader(Program program) {
        header.println("/* Just 语言编译器生成的 C 代码 */");
        header.println("#ifndef JUST_GENERATED_H");
        header.println("#define JUST_GENERATED_H");
        header.println();
        header.println("#include <stdio.h>");
        header.println("#include <stdlib.h>");
        header.println("#include <string.h>");
        header.println("#include <stdbool.h>");
        header.println("#include \"runtime.h\"");
        header.println();

        // 前向声明所有类
        for (ClassDecl classDecl : program.classes) {
            header.println("typedef struct " + classDecl.name + " " +
                          classDecl.name + ";");
        }
        header.println();

        // 生成类结构体定义
        for (ClassDecl classDecl : program.classes) {
            generateClassHeader(classDecl);
        }

        header.println("#endif");
    }

    private void generateClassHeader(ClassDecl classDecl) {
        header.println("/* 类: " + classDecl.name + " */");
        header.println("struct " + classDecl.name + " {");

        // 继承父类字段
        if (classDecl.superClass != null) {
            header.println("    /* 继承自 " + classDecl.superClass + " */");
            ClassInfo superClass = classes.get(classDecl.superClass);
            if (superClass != null) {
                for (ClassInfo.FieldInfo field : superClass.fields.values()) {
                    header.println("    " + toCType(field.type, field.isArray) +
                                  " " + field.name + ";");
                }
            }
        }

        // 字段
        for (FieldDecl field : classDecl.fields) {
            header.println("    " + toCType(field.type.name, field.type.isArray) +
                          " " + field.name + ";");
        }

        header.println("};");
        header.println();

        // 方法声明
        for (MethodDecl method : classDecl.methods) {
            generateMethodSignature(header, classDecl.name, method);
            header.println(";");
        }
        header.println();
    }

    private void generateSource(Program program) {
        source.println("/* Just 语言编译器生成的 C 代码 */");
        source.println("#include \"just_generated.h\"");
        source.println();

        // 生成所有方法实现
        for (ClassDecl classDecl : program.classes) {
            currentClass = classes.get(classDecl.name);

            // 检查是否有构造函数
            boolean hasConstructor = false;
            for (MethodDecl method : classDecl.methods) {
                if (method.isConstructor) {
                    hasConstructor = true;
                }
                generateMethodImpl(classDecl.name, method);
            }

            // 如果没有构造函数，生成默认构造函数
            if (!hasConstructor) {
                generateDefaultConstructor(classDecl.name);
            }
        }

        // 生成 main 函数
        generateMainFunction(program);
    }

    /**
     * 生成默认构造函数
     */
    private void generateDefaultConstructor(String className) {
        source.println("/* " + className + "." + className + " (默认构造函数) */");
        source.println(className + "* " + className + "_" + className + "() {");
        source.println("    " + className + "* self = (" + className +
                      "*)just_alloc(sizeof(" + className + "));");
        source.println("    return self;");
        source.println("}");
        source.println();
    }

    private void generateMethodImpl(String className, MethodDecl method) {
        // 清空局部变量类型映射
        localVariableTypes.clear();

        List<String> paramTypes = new ArrayList<>();
        for (Parameter param : method.parameters) {
            paramTypes.add(param.type.name + (param.type.isArray ? "[]" : ""));
        }
        currentMethod = currentClass.getMethod(method.name, paramTypes);

        source.println("/* " + className + "." + method.name + " */");
        generateMethodSignature(source, className, method);
        source.println(" {");
        indentLevel++;

        // 构造函数：分配内存
        if (method.isConstructor) {
            indent(source);
            source.println(className + "* self = (" + className +
                          "*)just_alloc(sizeof(" + className + "));");

            // 构造函数简化语法：自动赋值
            if (method.body.statements.isEmpty()) {
                for (Parameter param : method.parameters) {
                    indent(source);
                    source.println("self->" + param.name + " = " + param.name + ";");
                }
            }
        }

        // 生成方法体
        for (Stmt stmt : method.body.statements) {
            stmt.accept(this);
        }

        // 构造函数：返回 self
        if (method.isConstructor) {
            indent(source);
            source.println("return self;");
        }

        indentLevel--;
        source.println("}");
        source.println();
    }

    private void generateMethodSignature(PrintWriter out, String className,
                                         MethodDecl method) {
        // 返回类型
        if (method.isConstructor) {
            out.print(className + "*");
        } else {
            out.print(toCType(method.returnType.name, method.returnType.isArray));
        }

        out.print(" ");

        // 方法名
        if (method.isOperator) {
            out.print(className + "_" + method.name.replace("operator", "op"));
        } else {
            out.print(className + "_" + method.name);
        }

        out.print("(");

        // 参数列表
        List<String> params = new ArrayList<>();
        if (!method.isConstructor) {
            params.add(className + "* self");
        }

        for (Parameter param : method.parameters) {
            params.add(toCType(param.type.name, param.type.isArray) +
                      " " + param.name);
        }

        out.print(String.join(", ", params));
        out.print(")");
    }

    private void generateMainFunction(Program program) {
        source.println("/* 程序入口 */");
        source.println("int main(int argc, char** argv) {");
        source.println("    just_runtime_init();");
        source.println();

        // 查找 Main 类的 main 方法
        for (ClassDecl classDecl : program.classes) {
            if (classDecl.name.equals("Main")) {
                source.println("    Main* main_obj = Main_Main();");
                source.println("    Main_main(main_obj);");
                break;
            }
        }

        source.println();
        source.println("    return 0;");
        source.println("}");
    }

    // ==================== AST 访问器实现 ====================

    @Override
    public Void visitProgram(Program node) {
        return null;
    }

    @Override
    public Void visitClassDecl(ClassDecl node) {
        return null;
    }

    @Override
    public Void visitMethodDecl(MethodDecl node) {
        return null;
    }

    @Override
    public Void visitFieldDecl(FieldDecl node) {
        return null;
    }

    @Override
    public Void visitParameter(Parameter node) {
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt node) {
        for (Stmt stmt : node.statements) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt node) {
        indent(source);
        source.print("if (");
        node.condition.accept(this);
        source.println(") {");
        indentLevel++;
        node.thenBranch.accept(this);
        indentLevel--;
        indent(source);
        if (node.elseBranch != null) {
            source.println("} else {");
            indentLevel++;
            node.elseBranch.accept(this);
            indentLevel--;
            indent(source);
        }
        source.println("}");
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt node) {
        indent(source);
        source.print("while (");
        node.condition.accept(this);
        source.println(") {");
        indentLevel++;
        node.body.accept(this);
        indentLevel--;
        indent(source);
        source.println("}");
        return null;
    }

    @Override
    public Void visitForStmt(ForStmt node) {
        if (node.isEnhanced) {
            // TODO: 增强 for 循环
            indent(source);
            source.println("/* 增强 for 循环 - TODO */");
        } else {
            indent(source);
            source.print("for (");
            if (node.initializer != null) {
                // 初始化器：可能是变量声明或表达式
                if (node.initializer instanceof VarDeclStmt) {
                    VarDeclStmt varDecl = (VarDeclStmt) node.initializer;
                    source.print(toCType(varDecl.type.name, varDecl.type.isArray));
                    source.print(" ");
                    source.print(varDecl.name);
                    if (varDecl.initializer != null) {
                        source.print(" = ");
                        varDecl.initializer.accept(this);
                    }
                } else {
                    node.initializer.accept(this);
                }
            }
            source.print("; ");
            if (node.condition != null) {
                node.condition.accept(this);
            }
            source.print("; ");
            if (node.increment != null) {
                node.increment.accept(this);
            }
            source.println(") {");
            indentLevel++;
            node.body.accept(this);
            indentLevel--;
            indent(source);
            source.println("}");
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        indent(source);
        source.print("return");
        if (node.value != null) {
            source.print(" ");
            node.value.accept(this);
        }
        source.println(";");
        return null;
    }

    @Override
    public Void visitBreakStmt(BreakStmt node) {
        indent(source);
        source.println("break;");
        return null;
    }

    @Override
    public Void visitContinueStmt(ContinueStmt node) {
        indent(source);
        source.println("continue;");
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt node) {
        indent(source);
        node.expression.accept(this);
        source.println(";");
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclStmt node) {
        // 记录局部变量类型
        localVariableTypes.put(node.name, node.type.name);

        indent(source);
        source.print(toCType(node.type.name, node.type.isArray));
        source.print(" " + node.name);
        if (node.initializer != null) {
            source.print(" = ");
            node.initializer.accept(this);
        }
        source.println(";");
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr node) {
        source.print("(");
        node.left.accept(this);
        source.print(" " + node.operator + " ");
        node.right.accept(this);
        source.print(")");
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr node) {
        if (node.isPrefix) {
            source.print(node.operator);
            node.operand.accept(this);
        } else {
            node.operand.accept(this);
            source.print(node.operator);
        }
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr node) {
        // 检查是否是内置函数
        if (node.callee instanceof IdentifierExpr) {
            IdentifierExpr funcName = (IdentifierExpr) node.callee;

            // println 内置函数
            if (funcName.name.equals("println")) {
                if (node.arguments.isEmpty()) {
                    source.print("printf(\"\\n\")");
                } else if (node.arguments.size() == 1) {
                    Expr arg = node.arguments.get(0);
                    // 检测参数类型并使用合适的打印函数
                    if (arg instanceof LiteralExpr) {
                        LiteralExpr lit = (LiteralExpr) arg;
                        if (lit.value instanceof String) {
                            source.print("just_println_string(\"");
                            source.print(lit.value);
                            source.print("\")");
                            return null;
                        } else if (lit.value instanceof Integer || lit.value instanceof Long) {
                            source.print("just_println_int(");
                            arg.accept(this);
                            source.print(")");
                            return null;
                        }
                    }
                    // 默认：尝试作为字符串打印
                    source.print("printf(\"%d\\n\", ");
                    arg.accept(this);
                    source.print(")");
                }
                return null;
            }

            // print 内置函数
            if (funcName.name.equals("print")) {
                if (node.arguments.size() == 1) {
                    Expr arg = node.arguments.get(0);
                    if (arg instanceof LiteralExpr) {
                        LiteralExpr lit = (LiteralExpr) arg;
                        if (lit.value instanceof String) {
                            source.print("printf(\"");
                            source.print(lit.value);
                            source.print("\")");
                            return null;
                        }
                    }
                    source.print("printf(\"%d\", ");
                    arg.accept(this);
                    source.print(")");
                }
                return null;
            }
        }

        // 普通方法调用
        if (node.callee instanceof MemberExpr) {
            MemberExpr member = (MemberExpr) node.callee;
            // obj.method(args) -> ClassName_method(obj, args)
            if (member.object instanceof IdentifierExpr) {
                IdentifierExpr obj = (IdentifierExpr) member.object;

                // 查找对象的实际类型
                String typeName = localVariableTypes.get(obj.name);
                if (typeName == null && currentClass != null && currentClass.hasField(obj.name)) {
                    // 如果不是局部变量，检查是否是字段
                    ClassInfo.FieldInfo field = currentClass.getField(obj.name);
                    if (field != null) {
                        typeName = field.type;
                    }
                }

                if (typeName == null) {
                    // 回退：使用变量名首字母大写
                    typeName = Character.toUpperCase(obj.name.charAt(0)) + obj.name.substring(1);
                }

                // 生成：TypeName_methodName(obj, args)
                source.print(typeName + "_" + member.member + "(");
                obj.accept(this);
                if (!node.arguments.isEmpty()) {
                    source.print(", ");
                }
            } else {
                // 复杂对象表达式
                source.print("/* TODO: complex method call */");
                source.print("(");
            }
        } else {
            // 函数调用
            node.callee.accept(this);
            source.print("(");
        }

        // 参数
        for (int i = 0; i < node.arguments.size(); i++) {
            if (i > 0) source.print(", ");
            node.arguments.get(i).accept(this);
        }
        source.print(")");

        return null;
    }

    @Override
    public Void visitMemberExpr(MemberExpr node) {
        node.object.accept(this);
        source.print("->" + node.member);
        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExpr node) {
        node.array.accept(this);
        source.print("[");
        node.index.accept(this);
        source.print("]");
        return null;
    }

    @Override
    public Void visitNewExpr(NewExpr node) {
        if (node.arraySize != null) {
            source.print("just_alloc_array(sizeof(" +
                        toCType(node.type.name, false) + "), ");
            node.arraySize.accept(this);
            source.print(")");
        } else {
            source.print(node.type.name + "_" + node.type.name + "(");
            for (int i = 0; i < node.arguments.size(); i++) {
                if (i > 0) source.print(", ");
                node.arguments.get(i).accept(this);
            }
            source.print(")");
        }
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr node) {
        if (node.value == null) {
            source.print("NULL");
        } else if (node.value instanceof Boolean) {
            source.print((Boolean) node.value ? "true" : "false");
        } else if (node.value instanceof String) {
            source.print("\"" + escape((String) node.value) + "\"");
        } else if (node.value instanceof Character) {
            source.print("'" + escape(String.valueOf(node.value)) + "'");
        } else {
            source.print(node.value.toString());
        }
        return null;
    }

    @Override
    public Void visitIdentifierExpr(IdentifierExpr node) {
        // 检查是否是当前类的字段
        if (currentClass != null && currentClass.hasField(node.name)) {
            source.print("self->" + node.name);
        } else {
            source.print(node.name);
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr node) {
        node.target.accept(this);
        source.print(" " + node.operator + " ");
        node.value.accept(this);
        return null;
    }

    @Override
    public Void visitThisExpr(ThisExpr node) {
        source.print("self");
        return null;
    }

    @Override
    public Void visitSuperExpr(SuperExpr node) {
        source.print("/* super." + node.member + " */");
        return null;
    }

    @Override
    public Void visitArrayLiteralExpr(ArrayLiteralExpr node) {
        source.print("/* array literal - TODO */");
        return null;
    }

    @Override
    public Void visitStringInterpolationExpr(StringInterpolationExpr node) {
        source.print("just_string_concat(");
        // TODO: 字符串插值
        source.print(")");
        return null;
    }

    @Override
    public Void visitType(Type node) {
        return null;
    }

    // ==================== 辅助方法 ====================

    private String toCType(String type, boolean isArray) {
        String cType;
        switch (type) {
            case "int": cType = "int"; break;
            case "long": cType = "long long"; break;
            case "float": cType = "float"; break;
            case "double": cType = "double"; break;
            case "bool": cType = "bool"; break;
            case "char": cType = "char"; break;
            case "string": cType = "char*"; break;
            case "void": cType = "void"; break;
            default: cType = type + "*"; break;
        }

        if (isArray && !type.equals("string")) {
            cType += "*";
        }

        return cType;
    }

    private void indent(PrintWriter out) {
        for (int i = 0; i < indentLevel; i++) {
            out.print("    ");
        }
    }

    private String escape(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\t", "\\t")
                  .replace("\r", "\\r");
    }
}
