package just.semantic;
import just.ast.ASTNodes.*;

import just.ast.*;
import java.util.*;

/**
 * 语义分析器 - 类型检查和作用域分析
 */
public class SemanticAnalyzer implements ASTVisitor<String> {
    private final Map<String, ClassInfo> classes = new HashMap<>();
    private SymbolTable currentScope;
    private ClassInfo currentClass;
    private ClassInfo.MethodInfo currentMethod;
    private final List<String> errors = new ArrayList<>();

    // 内置类型
    private static final Set<String> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
        "int", "long", "float", "double", "bool", "char", "string", "void"
    ));

    // 内置函数
    private static final Set<String> BUILTIN_FUNCTIONS = new HashSet<>(Arrays.asList(
        "println", "print", "readln", "toString", "parseInt", "parseFloat"
    ));

    public void analyze(Program program) {
        // 第一遍：收集所有类的信息
        for (ClassDecl classDecl : program.classes) {
            collectClassInfo(classDecl);
        }

        // 第二遍：检查继承关系
        for (ClassDecl classDecl : program.classes) {
            checkInheritance(classDecl);
        }

        // 第三遍：类型检查
        currentScope = new SymbolTable("global", null);
        for (ClassDecl classDecl : program.classes) {
            classDecl.accept(this);
        }

        // 报告错误
        if (!errors.isEmpty()) {
            System.err.println("=== 语义分析错误 ===");
            for (String error : errors) {
                System.err.println(error);
            }
            throw new RuntimeException("语义分析失败，发现 " + errors.size() + " 个错误");
        }

        System.out.println("语义分析成功！");
    }

    private void collectClassInfo(ClassDecl classDecl) {
        if (classes.containsKey(classDecl.name)) {
            error(classDecl, "类 '" + classDecl.name + "' 重复定义");
            return;
        }

        ClassInfo classInfo = new ClassInfo(classDecl.name, classDecl.superClass,
                                            classDecl.interfaces,
                                            classDecl.line, classDecl.column);

        // 收集字段
        for (FieldDecl field : classDecl.fields) {
            ClassInfo.FieldInfo fieldInfo = new ClassInfo.FieldInfo(
                field.name,
                field.type.name,
                field.type.isArray,
                field.isPublic,
                field.line,
                field.column
            );
            classInfo.addField(fieldInfo);
        }

        // 收集方法
        for (MethodDecl method : classDecl.methods) {
            List<String> paramTypes = new ArrayList<>();
            List<String> paramNames = new ArrayList<>();
            for (Parameter param : method.parameters) {
                paramTypes.add(param.type.name + (param.type.isArray ? "[]" : ""));
                paramNames.add(param.name);
            }

            ClassInfo.MethodInfo methodInfo = new ClassInfo.MethodInfo(
                method.name,
                method.returnType.name,
                method.returnType.isArray,
                paramTypes,
                paramNames,
                method.isPublic,
                method.isConstructor,
                method.isOperator,
                method.line,
                method.column
            );
            classInfo.addMethod(methodInfo);
        }

        classes.put(classDecl.name, classInfo);
    }

    private void checkInheritance(ClassDecl classDecl) {
        if (classDecl.superClass == null) {
            return;
        }

        if (!classes.containsKey(classDecl.superClass)) {
            error(classDecl, "父类 '" + classDecl.superClass + "' 不存在");
        }

        // TODO: 检查循环继承
    }

    // ==================== AST 访问器实现 ====================

    @Override
    public String visitProgram(Program node) {
        for (ClassDecl classDecl : node.classes) {
            classDecl.accept(this);
        }
        return null;
    }

    @Override
    public String visitClassDecl(ClassDecl node) {
        currentClass = classes.get(node.name);
        currentScope = new SymbolTable("class:" + node.name, currentScope);

        // 添加字段到作用域
        for (FieldDecl field : node.fields) {
            Symbol symbol = new Symbol(
                field.name,
                Symbol.Kind.FIELD,
                field.type.name,
                field.type.isArray,
                field.isPublic,
                field.line,
                field.column
            );
            currentScope.define(symbol);
        }

        // 检查方法
        for (MethodDecl method : node.methods) {
            method.accept(this);
        }

        currentScope = currentScope.getParent();
        currentClass = null;
        return null;
    }

    @Override
    public String visitMethodDecl(MethodDecl node) {
        List<String> paramTypes = new ArrayList<>();
        for (Parameter param : node.parameters) {
            paramTypes.add(param.type.name + (param.type.isArray ? "[]" : ""));
        }
        currentMethod = currentClass.getMethod(node.name, paramTypes);

        currentScope = new SymbolTable("method:" + node.name, currentScope);

        // 添加参数到作用域
        for (Parameter param : node.parameters) {
            Symbol symbol = new Symbol(
                param.name,
                Symbol.Kind.VARIABLE,
                param.type.name,
                param.type.isArray,
                true,
                param.line,
                param.column
            );
            currentScope.define(symbol);
        }

        // 检查方法体
        if (node.body != null) {
            node.body.accept(this);
        }

        currentScope = currentScope.getParent();
        currentMethod = null;
        return null;
    }

    @Override
    public String visitFieldDecl(FieldDecl node) {
        checkType(node.type);
        if (node.initializer != null) {
            String initType = node.initializer.accept(this);
            if (!isCompatible(node.type.name, node.type.isArray, initType)) {
                error(node, "字段初始化类型不匹配: 期望 " +
                           node.type + ", 实际 " + initType);
            }
        }
        return null;
    }

    @Override
    public String visitParameter(Parameter node) {
        checkType(node.type);
        return null;
    }

    @Override
    public String visitBlockStmt(BlockStmt node) {
        currentScope = new SymbolTable("block", currentScope);
        for (Stmt stmt : node.statements) {
            stmt.accept(this);
        }
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitIfStmt(IfStmt node) {
        String condType = node.condition.accept(this);
        if (!condType.equals("bool")) {
            error(node, "if 条件必须是 bool 类型，实际是 " + condType);
        }
        node.thenBranch.accept(this);
        if (node.elseBranch != null) {
            node.elseBranch.accept(this);
        }
        return null;
    }

    @Override
    public String visitWhileStmt(WhileStmt node) {
        String condType = node.condition.accept(this);
        if (!condType.equals("bool")) {
            error(node, "while 条件必须是 bool 类型，实际是 " + condType);
        }
        node.body.accept(this);
        return null;
    }

    @Override
    public String visitForStmt(ForStmt node) {
        currentScope = new SymbolTable("for", currentScope);

        if (node.isEnhanced) {
            // 增强 for 循环
            checkType(node.varType);
            String iterableType = node.iterable.accept(this);

            // TODO: 检查 iterable 是数组还是区间表达式

            Symbol loopVar = new Symbol(
                node.varName,
                Symbol.Kind.VARIABLE,
                node.varType.name,
                false,
                true,
                node.line,
                node.column
            );
            currentScope.define(loopVar);
        } else {
            // 传统 for 循环
            if (node.initializer != null) {
                node.initializer.accept(this);
            }
            if (node.condition != null) {
                String condType = node.condition.accept(this);
                if (!condType.equals("bool")) {
                    error(node, "for 条件必须是 bool 类型");
                }
            }
            if (node.increment != null) {
                node.increment.accept(this);
            }
        }

        node.body.accept(this);
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public String visitReturnStmt(ReturnStmt node) {
        if (currentMethod == null) {
            error(node, "return 语句只能在方法中使用");
            return null;
        }

        String returnType = currentMethod.returnType;
        if (node.value == null) {
            if (!returnType.equals("void")) {
                error(node, "方法应返回 " + returnType + " 类型");
            }
        } else {
            String valueType = node.value.accept(this);
            if (!isCompatible(returnType, currentMethod.returnIsArray, valueType)) {
                error(node, "返回类型不匹配: 期望 " + returnType +
                           ", 实际 " + valueType);
            }
        }
        return null;
    }

    @Override
    public String visitBreakStmt(BreakStmt node) {
        // TODO: 检查是否在循环中
        return null;
    }

    @Override
    public String visitContinueStmt(ContinueStmt node) {
        // TODO: 检查是否在循环中
        return null;
    }

    @Override
    public String visitExprStmt(ExprStmt node) {
        node.expression.accept(this);
        return null;
    }

    @Override
    public String visitVarDeclStmt(VarDeclStmt node) {
        checkType(node.type);

        Symbol symbol = new Symbol(
            node.name,
            Symbol.Kind.VARIABLE,
            node.type.name,
            node.type.isArray,
            true,
            node.line,
            node.column
        );
        currentScope.define(symbol);

        if (node.initializer != null) {
            String initType = node.initializer.accept(this);
            if (!isCompatible(node.type.name, node.type.isArray, initType)) {
                error(node, "变量初始化类型不匹配: 期望 " +
                           node.type + ", 实际 " + initType);
            }
        }
        return null;
    }

    @Override
    public String visitBinaryExpr(BinaryExpr node) {
        String leftType = node.left.accept(this);
        String rightType = node.right.accept(this);

        switch (node.operator) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return promoteType(leftType, rightType);
                }
                if (node.operator.equals("+") && (leftType.equals("string") ||
                                                  rightType.equals("string"))) {
                    return "string";
                }
                error(node, "运算符 " + node.operator + " 的操作数类型不匹配");
                return "int";

            case "==":
            case "!=":
            case "<":
            case ">":
            case "<=":
            case ">=":
                return "bool";

            case "&&":
            case "||":
                if (!leftType.equals("bool") || !rightType.equals("bool")) {
                    error(node, "逻辑运算符需要 bool 类型操作数");
                }
                return "bool";

            default:
                error(node, "未知的运算符: " + node.operator);
                return "int";
        }
    }

    @Override
    public String visitUnaryExpr(UnaryExpr node) {
        String operandType = node.operand.accept(this);

        switch (node.operator) {
            case "-":
            case "++":
            case "--":
                if (!isNumeric(operandType)) {
                    error(node, "运算符 " + node.operator + " 需要数值类型");
                }
                return operandType;

            case "!":
                if (!operandType.equals("bool")) {
                    error(node, "运算符 ! 需要 bool 类型");
                }
                return "bool";

            default:
                error(node, "未知的一元运算符: " + node.operator);
                return operandType;
        }
    }

    @Override
    public String visitCallExpr(CallExpr node) {
        // 检查是否是内置函数
        if (node.callee instanceof IdentifierExpr) {
            String funcName = ((IdentifierExpr) node.callee).name;
            if (BUILTIN_FUNCTIONS.contains(funcName)) {
                // 内置函数返回类型
                if (funcName.equals("parseInt")) {
                    return "int";
                } else if (funcName.equals("parseFloat")) {
                    return "float";
                } else if (funcName.equals("toString") || funcName.equals("readln")) {
                    return "string";
                } else {
                    // println, print 返回 void
                    return "void";
                }
            }
        }

        // 方法调用 obj.method()
        if (node.callee instanceof MemberExpr) {
            MemberExpr member = (MemberExpr) node.callee;
            String objectType = member.object.accept(this);

            // 查找方法
            ClassInfo classInfo = classes.get(objectType);
            if (classInfo != null) {
                // 构建参数类型列表
                List<String> argTypes = new ArrayList<>();
                for (Expr arg : node.arguments) {
                    argTypes.add(arg.accept(this));
                }

                // 查找方法
                ClassInfo.MethodInfo method = classInfo.getMethod(member.member, argTypes);
                if (method != null) {
                    return method.returnType;
                }

                // 方法未找到，报错
                error(node, "类 " + objectType + " 中未找到方法 " + member.member);
                return "void";
            }
        }

        // 默认返回 void
        return "void";
    }

    @Override
    public String visitMemberExpr(MemberExpr node) {
        String objectType = node.object.accept(this);
        // TODO: 成员访问类型检查
        return "int";
    }

    @Override
    public String visitIndexExpr(IndexExpr node) {
        String arrayType = node.array.accept(this);
        String indexType = node.index.accept(this);

        if (!indexType.equals("int")) {
            error(node, "数组索引必须是 int 类型");
        }

        if (arrayType.endsWith("[]")) {
            return arrayType.substring(0, arrayType.length() - 2);
        }

        error(node, "只能对数组使用索引访问");
        return "int";
    }

    @Override
    public String visitNewExpr(NewExpr node) {
        checkType(node.type);
        if (node.arraySize != null) {
            String sizeType = node.arraySize.accept(this);
            if (!sizeType.equals("int")) {
                error(node, "数组大小必须是 int 类型");
            }
            return node.type.name + "[]";
        }
        return node.type.name;
    }

    @Override
    public String visitLiteralExpr(LiteralExpr node) {
        if (node.value == null) return "null";
        if (node.value instanceof Boolean) return "bool";
        if (node.value instanceof Long) return "int";
        if (node.value instanceof Double) return "float";
        if (node.value instanceof Character) return "char";
        if (node.value instanceof String) return "string";
        return "unknown";
    }

    @Override
    public String visitIdentifierExpr(IdentifierExpr node) {
        // 检查是否是内置函数
        if (BUILTIN_FUNCTIONS.contains(node.name)) {
            return "void";  // 内置函数暂时返回 void
        }

        Symbol symbol = currentScope.resolve(node.name);
        if (symbol == null) {
            error(node, "未定义的标识符: " + node.name);
            return "int";
        }
        return symbol.getFullType();
    }

    @Override
    public String visitAssignExpr(AssignExpr node) {
        String targetType = node.target.accept(this);
        String valueType = node.value.accept(this);

        if (!isCompatible(targetType, valueType)) {
            error(node, "赋值类型不匹配: " + targetType + " = " + valueType);
        }

        return targetType;
    }

    @Override
    public String visitThisExpr(ThisExpr node) {
        if (currentClass == null) {
            error(node, "this 只能在类中使用");
            return "unknown";
        }
        return currentClass.name;
    }

    @Override
    public String visitSuperExpr(SuperExpr node) {
        if (currentClass == null || currentClass.superClass == null) {
            error(node, "super 只能在有父类的类中使用");
            return "unknown";
        }
        return currentClass.superClass;
    }

    @Override
    public String visitArrayLiteralExpr(ArrayLiteralExpr node) {
        if (node.elements.isEmpty()) {
            return "int[]";
        }
        String elementType = node.elements.get(0).accept(this);
        for (int i = 1; i < node.elements.size(); i++) {
            String type = node.elements.get(i).accept(this);
            if (!type.equals(elementType)) {
                error(node, "数组元素类型不一致");
            }
        }
        return elementType + "[]";
    }

    @Override
    public String visitStringInterpolationExpr(StringInterpolationExpr node) {
        for (Object part : node.parts) {
            if (part instanceof Expr) {
                ((Expr) part).accept(this);
            }
        }
        return "string";
    }

    @Override
    public String visitType(Type node) {
        checkType(node);
        return null;
    }

    // ==================== 辅助方法 ====================

    private void checkType(Type type) {
        if (!PRIMITIVE_TYPES.contains(type.name) &&
            !classes.containsKey(type.name)) {
            error(type, "未定义的类型: " + type.name);
        }
    }

    private boolean isNumeric(String type) {
        return type.equals("int") || type.equals("long") ||
               type.equals("float") || type.equals("double");
    }

    private String promoteType(String type1, String type2) {
        if (type1.equals("double") || type2.equals("double")) return "double";
        if (type1.equals("float") || type2.equals("float")) return "float";
        if (type1.equals("long") || type2.equals("long")) return "long";
        return "int";
    }

    private boolean isCompatible(String targetType, boolean targetIsArray,
                                  String sourceType) {
        String target = targetType + (targetIsArray ? "[]" : "");
        return isCompatible(target, sourceType);
    }

    private boolean isCompatible(String targetType, String sourceType) {
        if (targetType.equals(sourceType)) return true;
        if (sourceType.equals("null")) return !PRIMITIVE_TYPES.contains(targetType);

        // 数值类型转换
        if (targetType.equals("long") && sourceType.equals("int")) return true;
        if (targetType.equals("float") && isNumeric(sourceType)) return true;
        if (targetType.equals("double") && isNumeric(sourceType)) return true;

        // TODO: 检查继承关系

        return false;
    }

    private void error(ASTNode node, String message) {
        errors.add(String.format("[语义错误] %d:%d - %s",
                                node.line, node.column, message));
    }

    public Map<String, ClassInfo> getClasses() {
        return classes;
    }
}
