package just.simple;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SimpleJustCompiler {
    private static String errorFile = "<source>";
    private static String[] errorLines = new String[0];
    private final String source;
    private final String outputFile;
    private final Map<String, TypeDef> types = new LinkedHashMap<>();
    private final Map<String, FunctionDef> functions = new LinkedHashMap<>();
    private final Map<String, String> variables = new LinkedHashMap<>();
    private final List<Stmt> main = new ArrayList<>();
    private String parseSelfType = null;

    public SimpleJustCompiler(String source, String outputFile) {
        this.source = source;
        this.outputFile = outputFile;
        errorFile = outputFile.endsWith(".just") ? outputFile : outputFile + ".just";
        errorLines = source.split("\\R", -1);
    }

    public static boolean looksLikeSimpleSyntax(String source) {
        for (String raw : source.split("\\R")) {
            String line = stripComment(raw).trim();
            if (line.isEmpty()) {
                continue;
            }
            return line.equals("main:") || line.startsWith("fn ") || isTypeHeader(line);
        }
        return false;
    }

    public void compile() throws IOException {
        parse();
        generate();
    }

    private void parse() {
        String[] lines = source.split("\\R");
        int i = 0;
        while (i < lines.length) {
            Line line = readLine(lines, i);
            i++;
            if (line == null) {
                continue;
            }
            if (line.indent != 0) {
                throw error(i, "Top-level declarations must not be indented");
            }
            if (line.text.equals("main:")) {
                i = parseBlock(lines, i, 4, main);
            } else if (line.text.startsWith("fn ") && line.text.endsWith(":")) {
                i = parseFunction(lines, i, line.text);
            } else if (isTypeHeader(line.text)) {
                i = parseType(lines, i, line.text.substring(0, line.text.length() - 1));
            } else {
                throw error(i, "Expected fn, main: or Type:");
            }
        }
    }

    private int parseFunction(String[] lines, int i, String header) {
        FunctionDef fn = parseFunctionHeader(header, i);
        functions.put(fn.name, fn);
        Map<String, String> saved = new LinkedHashMap<>(variables);
        String savedSelfType = parseSelfType;
        parseSelfType = null;
        for (ParameterDef param : fn.params) {
            variables.put(param.name, param.type);
        }
        i = parseBlock(lines, i, 4, fn.body);
        variables.clear();
        variables.putAll(saved);
        parseSelfType = savedSelfType;
        return i;
    }

    private int parseType(String[] lines, int i, String typeName) {
        TypeDef type = type(typeName);
        type.explicit = true;
        while (i < lines.length) {
            Line line = readLine(lines, i);
            i++;
            if (line == null) {
                continue;
            }
            if (line.indent < 4) {
                return i - 1;
            }
            if (line.indent != 4) {
                throw error(i, "Type members must use one indentation level");
            }
            if (line.text.endsWith(":")) {
                String methodHeader = line.text.substring(0, line.text.length() - 1).trim();
                MethodDef method = parseMethodHeader(methodHeader, i);
                Map<String, String> saved = new LinkedHashMap<>(variables);
                String savedSelfType = parseSelfType;
                parseSelfType = typeName;
                for (ParameterDef param : method.params) {
                    variables.put(param.name, param.type);
                }
                i = parseBlock(lines, i, 8, method.body);
                variables.clear();
                variables.putAll(saved);
                parseSelfType = savedSelfType;
                type.methods.put(method.name, method);
            } else {
                String[] parts = line.text.split("\\s+");
                if (parts.length != 2) {
                    throw error(i, "Expected field declaration: name type");
                }
                addField(type, parts[0], normalizeType(parts[1]), i);
            }
        }
        return i;
    }

    private int parseBlock(String[] lines, int i, int indent, List<Stmt> statements) {
        while (i < lines.length) {
            Line line = readLine(lines, i);
            i++;
            if (line == null) {
                continue;
            }
            if (line.indent < indent) {
                return i - 1;
            }
            if (line.indent != indent) {
                throw error(i, "Unexpected indentation");
            }
            if (line.text.endsWith(":")) {
                String header = line.text.substring(0, line.text.length() - 1).trim();
                if (header.startsWith("if ")) {
                    IfStmt stmt = new IfStmt(parseExpr(header.substring(3).trim()));
                    i = parseBlock(lines, i, indent + 4, stmt.thenBranch);
                    Line next = nextContentLine(lines, i);
                    if (next != null && next.indent == indent && next.text.equals("else:")) {
                        i++;
                        i = parseBlock(lines, i, indent + 4, stmt.elseBranch);
                    }
                    statements.add(stmt);
                    continue;
                }
                if (header.startsWith("for ")) {
                    ForStmt stmt = parseForHeader(header, i);
                    String arrayType = requireVariableType(stmt.arrayName, i);
                    if (!arrayType.endsWith("[]")) {
                        throw error(i, "for source is not an array: " + stmt.arrayName);
                    }
                    Map<String, String> saved = new LinkedHashMap<>(variables);
                    variables.put(stmt.itemName, elementType(arrayType));
                    i = parseBlock(lines, i, indent + 4, stmt.body);
                    variables.clear();
                    variables.putAll(saved);
                    statements.add(stmt);
                    continue;
                }
                if (header.startsWith("while ")) {
                    WhileStmt stmt = new WhileStmt(parseExpr(header.substring(6).trim()));
                    i = parseBlock(lines, i, indent + 4, stmt.body);
                    statements.add(stmt);
                    continue;
                }
                if (header.equals("else")) {
                    return i - 1;
                }
                if (header.contains(".")) {
                    String[] parts = header.split("\\.", 2);
                    String objectName = parts[0].trim();
                    String methodHeader = parts[1].trim();
                    String typeName = requireVariableType(objectName, i);
                    MethodDef method = parseMethodHeader(methodHeader, i);
                    Map<String, String> saved = new LinkedHashMap<>(variables);
                    String savedSelfType = parseSelfType;
                    parseSelfType = typeName;
                    for (ParameterDef param : method.params) {
                        variables.put(param.name, param.type);
                    }
                    i = parseBlock(lines, i, indent + 4, method.body);
                    variables.clear();
                    variables.putAll(saved);
                    parseSelfType = savedSelfType;
                    type(typeName).methods.put(method.name, method);
                    statements.add(new MethodAttachStmt(objectName, method.name));
                    continue;
                }
                throw error(i, "Only object.method: blocks are supported inside main");
            }
            if (line.text.endsWith("= [")) {
                ArrayDeclStmt array = parseArrayHeader(line.text, i);
                variables.put(array.name, array.type);
                i = parseArrayLiteral(lines, i, indent + 4, array);
                statements.add(array);
            } else {
                statements.add(parseStatement(line.text, i));
            }
        }
        return i;
    }

    private Stmt parseStatement(String text, int line) {
        if (text.startsWith("if ") && text.endsWith(":")) {
            throw error(line, "Internal parser error: if block reached statement parser");
        }
        if (text.startsWith("return ")) {
            return new ReturnStmt(parseExpr(text.substring(7).trim()));
        }
        String[] parts = text.split("\\s+", 3);
        if (parts.length >= 2 && isIdentifier(parts[0]) && isTypeName(parts[1])) {
            String name = parts[0];
            String typeName = normalizeType(parts[1]);
            Expr init = null;
            if (parts.length == 3) {
                if (!parts[2].startsWith("=")) {
                    throw error(line, "Expected '=' after variable declaration");
                }
                init = parseExpr(parts[2].substring(1).trim());
                validateInitializer(typeName, init, line);
            }
            variables.put(name, typeName);
            if (!typeName.endsWith("[]")) {
                type(typeName);
            }
            return new VarDeclStmt(typeName, name, init);
        }
        if (parts.length >= 2 && isTypeName(parts[0]) && isIdentifier(parts[1])) {
            String typeName = normalizeType(parts[0]);
            String name = parts[1];
            Expr init = null;
            if (parts.length == 3) {
                if (!parts[2].startsWith("=")) {
                    throw error(line, "Expected '=' after variable declaration");
                }
                init = parseExpr(parts[2].substring(1).trim());
                validateInitializer(typeName, init, line);
            }
            variables.put(name, typeName);
            if (!typeName.endsWith("[]")) {
                type(typeName);
            }
            return new VarDeclStmt(typeName, name, init);
        }
        int assign = findTopLevelAssign(text);
        if (assign >= 0) {
            String target = text.substring(0, assign).trim();
            Expr value = parseExpr(text.substring(assign + 1).trim());
            boolean declaresInferredVariable = target.indexOf('.') < 0 && !target.contains("[") &&
                !variables.containsKey(target) && !isFieldName(target, parseSelfType);
            inferAssignment(target, value, line);
            if (declaresInferredVariable) {
                return new VarDeclStmt(variables.get(target), target, value);
            }
            return new AssignStmt(target, value);
        }
        return new ExprStmt(parseExpr(text));
    }

    private void inferAssignment(String target, Expr value, int line) {
        MemberAccess member = splitMemberAccess(target);
        if (member == null) {
            if (isFieldName(target, parseSelfType) && !variables.containsKey(target)) {
                return;
            }
            variables.putIfAbsent(target, inferExprType(value, parseSelfType));
            return;
        }
        if (member.member.equals("len")) {
            throw error(line, "Cannot assign to array length");
        }
        String typeName = inferTargetType(member.receiver, parseSelfType);
        TypeDef type = type(typeName);
        if (type.explicit && !type.fields.containsKey(member.member)) {
            throw error(line, typeName + " has no field `" + member.member + "`.\nHint: Add `" + member.member + " " + inferExprType(value, parseSelfType) + "` under `" + typeName + ":` or remove this assignment.");
        }
        addField(type, member.member, inferType(value), line);
    }

    private Expr parseExpr(String text) {
        text = text.trim();
        if (isWrappedInParens(text)) {
            String body = text.substring(1, text.length() - 1).trim();
            if (hasTopLevelComma(body)) {
                List<Expr> args = new ArrayList<>();
                if (!body.isEmpty()) {
                    for (String part : splitArgs(body)) {
                        args.add(parseExpr(part));
                    }
                }
                return new TupleExpr(args);
            }
            return parseExpr(body);
        }
        for (String[] ops : new String[][] {
                {"||"},
                {"&&"},
                {"==", "!="},
                {">=", "<=", ">", "<"},
                {"+", "-"},
                {"*", "/", "%"}
        }) {
            OperatorMatch match = findTopLevelOperator(text, ops);
            if (match != null) {
                return new BinaryExpr(parseExpr(text.substring(0, match.index)), match.operator,
                                      parseExpr(text.substring(match.index + match.operator.length())));
            }
        }
        if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2) {
            return new StringExpr(text.substring(1, text.length() - 1));
        }
        if (text.equals("true") || text.equals("false")) {
            return new BoolExpr(Boolean.parseBoolean(text));
        }
        if (text.matches("-?\\d+")) {
            return new IntExpr(text);
        }
        if (text.matches("-?\\d+\\.\\d+")) {
            return new FloatExpr(text);
        }
        if (text.endsWith(")") && text.contains("(")) {
            int lp = text.indexOf('(');
            String callee = text.substring(0, lp).trim();
            String argsText = text.substring(lp + 1, text.length() - 1).trim();
            List<Expr> args = new ArrayList<>();
            if (!argsText.isEmpty()) {
                for (String part : splitArgs(argsText)) {
                    args.add(parseExpr(part));
                }
            }
            if (!callee.isEmpty() && Character.isUpperCase(callee.charAt(0))) {
                return new ConstructExpr(callee, args);
            }
            return new CallExpr(callee, args);
        }
        return new NameExpr(text);
    }

    private void generate() throws IOException {
        try (PrintWriter header = new PrintWriter(outputFile + ".h");
             PrintWriter sourceOut = new PrintWriter(outputFile + ".c")) {
            header.println("#ifndef JUST_GENERATED_H");
            header.println("#define JUST_GENERATED_H");
            header.println("#include <stdbool.h>");
            header.println("#include \"runtime.h\"");
            header.println();
            for (FunctionDef fn : functions.values()) {
                header.println(functionSignature(fn) + ";");
            }
            if (!functions.isEmpty()) {
                header.println();
            }
            for (TypeDef type : types.values()) {
                if (isPrimitive(type.name)) {
                    continue;
                }
                header.println("typedef struct " + type.name + " " + type.name + ";");
            }
            header.println();
            for (TypeDef type : types.values()) {
                if (isPrimitive(type.name)) {
                    continue;
                }
                header.println("struct " + type.name + " {");
                for (Map.Entry<String, String> field : type.fields.entrySet()) {
                    header.println("    " + cType(field.getValue()) + " " + field.getKey() + ";");
                }
                header.println("};");
                for (MethodDef method : type.methods.values()) {
                    header.println(methodSignature(type.name, method) + ";");
                }
                header.println();
            }
            header.println("#endif");

            sourceOut.println("#include <stdio.h>");
            sourceOut.println("#include <stdlib.h>");
            sourceOut.println("#include <string.h>");
            sourceOut.println("#include <stdarg.h>");
            sourceOut.println("#include \"just_generated.h\"");
            sourceOut.println();
            sourceOut.println("static char* just_simple_concat(const char* a, const char* b) {");
            sourceOut.println("    size_t len = strlen(a) + strlen(b) + 1;");
            sourceOut.println("    char* out = (char*)just_alloc(len);");
            sourceOut.println("    strcpy(out, a);");
            sourceOut.println("    strcat(out, b);");
            sourceOut.println("    return out;");
            sourceOut.println("}");
            sourceOut.println("static char* just_simple_format(const char* fmt, ...) {");
            sourceOut.println("    va_list args;");
            sourceOut.println("    va_start(args, fmt);");
            sourceOut.println("    va_list copy;");
            sourceOut.println("    va_copy(copy, args);");
            sourceOut.println("    int len = vsnprintf(NULL, 0, fmt, copy);");
            sourceOut.println("    va_end(copy);");
            sourceOut.println("    char* out = (char*)just_alloc((size_t)len + 1);");
            sourceOut.println("    vsnprintf(out, (size_t)len + 1, fmt, args);");
            sourceOut.println("    va_end(args);");
            sourceOut.println("    return out;");
            sourceOut.println("}");
            sourceOut.println();
            for (FunctionDef fn : functions.values()) {
                sourceOut.println(functionSignature(fn) + " {");
                Map<String, String> saved = new LinkedHashMap<>(variables);
                variables.clear();
                for (ParameterDef param : fn.params) {
                    variables.put(param.name, param.type);
                }
                emitStatements(sourceOut, fn.body, 1, null, fn.returnType);
                variables.clear();
                variables.putAll(saved);
                sourceOut.println("}");
                sourceOut.println();
            }
            for (TypeDef type : types.values()) {
                if (isPrimitive(type.name)) {
                    continue;
                }
                for (MethodDef method : type.methods.values()) {
                    sourceOut.println(methodSignature(type.name, method) + " {");
                    Map<String, String> saved = new LinkedHashMap<>(variables);
                    variables.clear();
                    for (ParameterDef param : method.params) {
                        variables.put(param.name, param.type);
                    }
                    emitStatements(sourceOut, method.body, 1, type.name, method.returnType);
                    variables.clear();
                    variables.putAll(saved);
                    sourceOut.println("}");
                    sourceOut.println();
                }
            }
            sourceOut.println("int main(int argc, char** argv) {");
            sourceOut.println("    just_runtime_init();");
            emitStatements(sourceOut, main, 1, null, "void");
            sourceOut.println("    return 0;");
            sourceOut.println("}");
        }
    }

    private void emitStatements(PrintWriter out, List<Stmt> statements, int indent, String selfType, String returnType) {
        for (int index = 0; index < statements.size(); index++) {
            Stmt stmt = statements.get(index);
            boolean isLast = index == statements.size() - 1;
            String pad = spaces(indent);
            if (stmt instanceof VarDeclStmt) {
                VarDeclStmt s = (VarDeclStmt) stmt;
                variables.put(s.name, s.type);
                out.print(pad + cType(s.type) + " " + s.name);
                if (s.init != null) {
                    out.print(" = " + emitExpr(s.init, selfType));
                } else if (!isPrimitive(s.type)) {
                    out.print(" = {0}");
                }
                out.println(";");
            } else if (stmt instanceof ArrayDeclStmt) {
                ArrayDeclStmt s = (ArrayDeclStmt) stmt;
                String itemType = elementType(s.type);
                out.println(pad + cType(itemType) + " " + s.name + "[] = {");
                for (int i = 0; i < s.elements.size(); i++) {
                    out.print(spaces(indent + 1) + emitExpr(s.elements.get(i), selfType));
                    out.println(i + 1 == s.elements.size() ? "" : ",");
                }
                out.println(pad + "};");
                out.println(pad + "int " + s.name + "_len = " + s.elements.size() + ";");
            } else if (stmt instanceof AssignStmt) {
                AssignStmt s = (AssignStmt) stmt;
                out.println(pad + emitTarget(s.target, selfType) + " = " + emitExpr(s.value, selfType) + ";");
            } else if (stmt instanceof ExprStmt) {
                Expr expr = ((ExprStmt) stmt).expr;
                if (isLast && !returnType.equals("void")) {
                    out.println(pad + "return " + emitExpr(expr, selfType) + ";");
                } else {
                    out.println(pad + emitExpr(expr, selfType) + ";");
                }
            } else if (stmt instanceof ReturnStmt) {
                out.println(pad + "return " + emitExpr(((ReturnStmt) stmt).expr, selfType) + ";");
            } else if (stmt instanceof IfStmt) {
                IfStmt s = (IfStmt) stmt;
                out.println(pad + "if (" + emitExpr(s.condition, selfType) + ") {");
                emitStatements(out, s.thenBranch, indent + 1, selfType, "void");
                out.println(pad + "}" + (s.elseBranch.isEmpty() ? "" : " else {"));
                if (!s.elseBranch.isEmpty()) {
                    emitStatements(out, s.elseBranch, indent + 1, selfType, "void");
                    out.println(pad + "}");
                }
            } else if (stmt instanceof WhileStmt) {
                WhileStmt s = (WhileStmt) stmt;
                out.println(pad + "while (" + emitExpr(s.condition, selfType) + ") {");
                emitStatements(out, s.body, indent + 1, selfType, "void");
                out.println(pad + "}");
            } else if (stmt instanceof ForStmt) {
                ForStmt s = (ForStmt) stmt;
                String arrayType = requireVariableType(s.arrayName, 0);
                String itemType = elementType(arrayType);
                String indexName = "__i" + indent + "_" + index;
                out.println(pad + "for (int " + indexName + " = 0; " + indexName + " < " + s.arrayName + "_len; " + indexName + "++) {");
                out.println(spaces(indent + 1) + cType(itemType) + " " + s.itemName + " = " + s.arrayName + "[" + indexName + "];");
                Map<String, String> saved = new LinkedHashMap<>(variables);
                variables.put(s.itemName, itemType);
                emitStatements(out, s.body, indent + 1, selfType, "void");
                variables.clear();
                variables.putAll(saved);
                out.println(pad + "}");
            }
        }
    }

    private String emitExpr(Expr expr, String selfType) {
        if (expr instanceof StringExpr) {
            StringExpr s = (StringExpr) expr;
            if (s.value.contains("${")) {
                return emitInterpolatedString(s.value, selfType);
            }
            return "\"" + escape(s.value) + "\"";
        }
        if (expr instanceof IntExpr) {
            return ((IntExpr) expr).value;
        }
        if (expr instanceof FloatExpr) {
            return ((FloatExpr) expr).value;
        }
        if (expr instanceof BoolExpr) {
            return ((BoolExpr) expr).value ? "true" : "false";
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr b = (BinaryExpr) expr;
            if (b.operator.equals("+") && inferExprType(b, selfType).equals("str")) {
                return "just_simple_concat(" + emitExpr(b.left, selfType) + ", " + emitExpr(b.right, selfType) + ")";
            }
            return "(" + emitExpr(b.left, selfType) + " " + b.operator + " " + emitExpr(b.right, selfType) + ")";
        }
        if (expr instanceof NameExpr) {
            return emitTarget(((NameExpr) expr).name, selfType);
        }
        if (expr instanceof ConstructExpr) {
            ConstructExpr c = (ConstructExpr) expr;
            List<String> args = new ArrayList<>();
            for (Expr arg : c.args) {
                args.add(emitExpr(arg, selfType));
            }
            return "(" + c.typeName + "){" + String.join(", ", args) + "}";
        }
        if (expr instanceof TupleExpr) {
            throw new RuntimeException("Tuple expression requires an array target type");
        }
        CallExpr call = (CallExpr) expr;
        if (call.callee.equals("print")) {
            if (call.args.size() != 1) {
                throw new RuntimeException("print expects one argument");
            }
            Expr arg = call.args.get(0);
            String argType = inferExprType(arg, selfType);
            if (arg instanceof StringExpr) {
                StringExpr s = (StringExpr) arg;
                if (s.value.contains("${")) {
                    return emitInterpolatedPrint(s.value, selfType, true);
                }
                return "printf(\"%s\\n\", " + emitExpr(arg, selfType) + ")";
            }
            if (argType.equals("bool")) {
                return "printf(\"%s\\n\", " + emitExpr(arg, selfType) + " ? \"true\" : \"false\")";
            }
            if (argType.equals("str")) {
                return "printf(\"%s\\n\", " + emitExpr(arg, selfType) + ")";
            }
            return "printf(\"%d\\n\", " + emitExpr(arg, selfType) + ")";
        }
        MemberAccess methodAccess = splitMemberAccess(call.callee);
        if (methodAccess != null) {
            String obj = methodAccess.receiver;
            String method = methodAccess.member;
            String typeName = inferTargetType(obj, selfType);
            MethodDef methodDef = requireMethod(typeName, method, call.args.size(), 0);
            List<String> args = new ArrayList<>();
            args.add("&" + emitTarget(obj, selfType));
            for (Expr arg : call.args) {
                args.add(emitExpr(arg, selfType));
            }
            return typeName + "_" + method + "(" + String.join(", ", args) + ")";
        }
        List<String> args = new ArrayList<>();
        for (Expr arg : call.args) {
            args.add(emitExpr(arg, selfType));
        }
        FunctionDef fn = functions.get(call.callee);
        if (fn != null && fn.params.size() != call.args.size()) {
            throw error(0, "Function `" + call.callee + "` expects " + fn.params.size() + " arguments, got " + call.args.size());
        }
        return call.callee + "(" + String.join(", ", args) + ")";
    }

    private String emitTarget(String target, String selfType) {
        if (target.startsWith("this.")) {
            return "self->" + target.substring(5);
        }
        if (variables.containsKey(target)) {
            return target;
        }
        MemberAccess member = splitMemberAccess(target);
        if (member != null) {
            if (member.member.equals("len")) {
                return member.receiver + "_len";
            }
            return emitTarget(member.receiver, selfType) + "." + member.member;
        }
        IndexAccess index = splitIndexAccess(target);
        if (index != null) {
            return emitTarget(index.arrayName, selfType) + "[" + emitExpr(parseExpr(index.indexExpr), selfType) + "]";
        }
        if (selfType != null && types.get(selfType).fields.containsKey(target)) {
            return "self->" + target;
        }
        return target;
    }

    private String emitInterpolatedPrint(String value, String selfType, boolean newline) {
        List<String> formats = new ArrayList<>();
        List<String> args = new ArrayList<>();
        int pos = 0;
        while (pos < value.length()) {
            int start = value.indexOf("${", pos);
            if (start < 0) {
                formats.add(escape(value.substring(pos)));
                break;
            }
            formats.add(escape(value.substring(pos, start)));
            int end = value.indexOf("}", start);
            if (end < 0) {
                throw new RuntimeException("Unclosed string interpolation");
            }
            String expr = value.substring(start + 2, end).trim();
            Expr parsed = parseExpr(expr);
            String exprType = inferExprType(parsed, selfType);
            formats.add(formatForType(exprType));
            args.add(interpolationArg(parsed, exprType, selfType));
            pos = end + 1;
        }
        String fmt = String.join("", formats) + (newline ? "\\n" : "");
        if (args.isEmpty()) {
            return "printf(\"" + fmt + "\")";
        }
        return "printf(\"" + fmt + "\", " + String.join(", ", args) + ")";
    }

    private String emitInterpolatedString(String value, String selfType) {
        List<String> formats = new ArrayList<>();
        List<String> args = new ArrayList<>();
        int pos = 0;
        while (pos < value.length()) {
            int start = value.indexOf("${", pos);
            if (start < 0) {
                formats.add(escape(value.substring(pos)));
                break;
            }
            formats.add(escape(value.substring(pos, start)));
            int end = value.indexOf("}", start);
            if (end < 0) {
                throw new RuntimeException("Unclosed string interpolation");
            }
            String expr = value.substring(start + 2, end).trim();
            Expr parsed = parseExpr(expr);
            String exprType = inferExprType(parsed, selfType);
            formats.add(formatForType(exprType));
            args.add(interpolationArg(parsed, exprType, selfType));
            pos = end + 1;
        }
        if (args.isEmpty()) {
            return "\"" + String.join("", formats) + "\"";
        }
        return "just_simple_format(\"" + String.join("", formats) + "\", " + String.join(", ", args) + ")";
    }

    private String interpolationArg(Expr expr, String exprType, String selfType) {
        String emitted = emitExpr(expr, selfType);
        if (exprType.equals("bool")) {
            return emitted + " ? \"true\" : \"false\"";
        }
        return emitted;
    }

    private String inferTargetType(String target, String selfType) {
        if (target.startsWith("this.") && selfType != null) {
            return types.get(selfType).fields.getOrDefault(target.substring(5), "str");
        }
        if (variables.containsKey(target)) {
            return variables.get(target);
        }
        MemberAccess member = splitMemberAccess(target);
        if (member != null) {
            if (member.member.equals("len")) {
                String receiverType = inferTargetType(member.receiver, selfType);
                if (receiverType.endsWith("[]")) {
                    return "int";
                }
            }
            String receiverType = inferTargetType(member.receiver, selfType);
            TypeDef type = types.get(receiverType);
            if (type == null || !type.fields.containsKey(member.member)) {
                throw error(0, "Undefined field `" + member.member + "` on `" + receiverType + "`");
            }
            return type.fields.get(member.member);
        }
        IndexAccess index = splitIndexAccess(target);
        if (index != null) {
            String arrayType = inferTargetType(index.arrayName, selfType);
            if (!arrayType.endsWith("[]")) {
                throw error(0, "Cannot index non-array variable `" + index.arrayName + "`");
            }
            return elementType(arrayType);
        }
        if (selfType != null && types.get(selfType).fields.containsKey(target)) {
            return types.get(selfType).fields.get(target);
        }
        return variables.getOrDefault(target, "str");
    }

    private String inferExprType(Expr expr, String selfType) {
        if (expr instanceof StringExpr) return "str";
        if (expr instanceof IntExpr) return "int";
        if (expr instanceof FloatExpr) return "float";
        if (expr instanceof BoolExpr) return "bool";
        if (expr instanceof BinaryExpr) {
            BinaryExpr b = (BinaryExpr) expr;
            if (b.operator.equals("+") &&
                (inferExprType(b.left, selfType).equals("str") || inferExprType(b.right, selfType).equals("str"))) {
                return "str";
            }
            if (b.operator.equals(">") || b.operator.equals(">=") || b.operator.equals("<") ||
                b.operator.equals("<=") || b.operator.equals("==") || b.operator.equals("!=") ||
                b.operator.equals("&&") || b.operator.equals("||")) {
                return "bool";
            }
            return inferExprType(b.left, selfType);
        }
        if (expr instanceof NameExpr) return inferTargetType(((NameExpr) expr).name, selfType);
        if (expr instanceof ConstructExpr) return ((ConstructExpr) expr).typeName;
        if (expr instanceof TupleExpr) return "tuple";
        CallExpr call = (CallExpr) expr;
        MemberAccess methodAccess = splitMemberAccess(call.callee);
        if (methodAccess != null) {
            String typeName = inferTargetType(methodAccess.receiver, selfType);
            TypeDef type = typeName == null ? null : types.get(typeName);
            MethodDef method = type == null ? null : type.methods.get(methodAccess.member);
            return method == null ? "int" : method.returnType;
        }
        FunctionDef fn = functions.get(call.callee);
        return fn == null ? "int" : fn.returnType;
    }

    private String inferType(Expr expr) {
        return inferExprType(expr, null);
    }

    private void addField(TypeDef type, String name, String fieldType, int line) {
        String old = type.fields.get(name);
        if (old != null && !old.equals(fieldType)) {
            throw error(line, "Field type conflict for " + type.name + "." + name + ": " + old + " vs " + fieldType);
        }
        type.fields.put(name, fieldType);
    }

    private void validateInitializer(String targetType, Expr init, int line) {
        targetType = normalizeType(targetType);
        if (targetType.endsWith("[]")) {
            return;
        }
        if (init instanceof ConstructExpr) {
            validateConstruct((ConstructExpr) init, targetType, line);
            return;
        }
        String initType = inferType(init);
        if (!targetType.equals(initType) && !targetType.equals("float")) {
            throw error(line, "Initializer type mismatch: expected " + targetType + ", got " + initType);
        }
    }

    private void validateConstruct(ConstructExpr init, String expectedType, int line) {
        if (!init.typeName.equals(expectedType)) {
            throw error(line, "Constructor type mismatch: expected " + expectedType + ", got " + init.typeName);
        }
        TypeDef type = types.get(init.typeName);
        if (type == null) {
            throw error(line, "Unknown type: " + init.typeName);
        }
        if (type.fields.size() != init.args.size()) {
            throw error(line, init.typeName + " constructor expects " + type.fields.size() + " arguments, got " + init.args.size());
        }
        int index = 0;
        for (String fieldType : type.fields.values()) {
            String argType = inferType(init.args.get(index));
            if (!fieldType.equals(argType) && !(fieldType.equals("float") && argType.equals("int"))) {
                throw error(line, "Constructor argument " + (index + 1) + " type mismatch: expected " + fieldType + ", got " + argType);
            }
            index++;
        }
    }

    private TypeDef type(String name) {
        name = normalizeType(name);
        return types.computeIfAbsent(name, TypeDef::new);
    }

    private String requireVariableType(String name, int line) {
        String typeName = variables.get(name);
        if (typeName == null) {
            throw error(line, "Unknown object variable: " + name);
        }
        return typeName;
    }

    private MethodDef requireMethod(String typeName, String methodName, int argCount, int line) {
        TypeDef type = types.get(typeName);
        if (type == null) {
            throw error(line, "Unknown type `" + typeName + "`");
        }
        MethodDef method = type.methods.get(methodName);
        if (method == null) {
            throw error(line, typeName + " has no method `" + methodName + "`");
        }
        if (method.params.size() != argCount) {
            throw error(line, "Method `" + typeName + "." + methodName + "` expects " + method.params.size() + " arguments, got " + argCount);
        }
        return method;
    }

    private boolean isFieldName(String name, String selfType) {
        return selfType != null && types.containsKey(selfType) && types.get(selfType).fields.containsKey(name);
    }

    private int parseArrayLiteral(String[] lines, int i, int elementIndent, ArrayDeclStmt array) {
        String itemType = elementType(array.type);
        while (i < lines.length) {
            Line line = readLine(lines, i);
            i++;
            if (line == null) {
                continue;
            }
            if (line.text.equals("]") && line.indent == elementIndent - 4) {
                return i;
            }
            if (line.indent != elementIndent) {
                throw error(i, "Array elements must use one indentation level");
            }
            Expr elem = parseExpr(line.text);
            if (elem instanceof TupleExpr) {
                elem = new ConstructExpr(itemType, ((TupleExpr) elem).args);
            }
            if (!(elem instanceof ConstructExpr)) {
                throw error(i, "Array element must be " + itemType + "(...) or tuple shorthand");
            }
            validateConstruct((ConstructExpr) elem, itemType, i);
            array.elements.add(elem);
        }
        throw error(i, "Unclosed array literal");
    }

    private static Line readLine(String[] lines, int index) {
        String raw = stripComment(lines[index]);
        if (raw.trim().isEmpty()) {
            return null;
        }
        int indent = 0;
        while (indent < raw.length() && raw.charAt(indent) == ' ') {
            indent++;
        }
        if (indent < raw.length() && raw.charAt(indent) == '\t') {
            throw new RuntimeException("Tabs are not supported for indentation");
        }
        return new Line(indent, raw.trim());
    }

    private static Line nextContentLine(String[] lines, int index) {
        int i = index;
        while (i < lines.length) {
            Line line = readLine(lines, i);
            if (line != null) {
                return line;
            }
            i++;
        }
        return null;
    }

    private static ArrayDeclStmt parseArrayHeader(String text, int line) {
        String compact = text.substring(0, text.length() - 3).trim();
        String[] parts = compact.split("\\s+");
        if (parts.length != 2 || !isIdentifier(parts[0]) || !parts[1].endsWith("[]")) {
            throw error(line, "Expected array declaration: name Type[] = [");
        }
        return new ArrayDeclStmt(normalizeType(parts[1]), parts[0]);
    }

    private static ForStmt parseForHeader(String header, int line) {
        String body = header.substring(4).trim();
        String[] parts = body.split("\\s+");
        if (parts.length != 3 || !parts[1].equals("in")) {
            throw error(line, "Expected for item in array:");
        }
        return new ForStmt(parts[0], parts[2]);
    }

    private static String stripComment(String raw) {
        boolean inString = false;
        for (int i = 0; i + 1 < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString && c == '/' && raw.charAt(i + 1) == '/') {
                return raw.substring(0, i);
            }
        }
        return raw;
    }

    private static boolean isTypeHeader(String text) {
        return text.endsWith(":") && Character.isUpperCase(text.charAt(0)) && text.indexOf(' ') < 0;
    }

    private static boolean isTypeName(String text) {
        if (text.endsWith("[]")) {
            return isTypeName(text.substring(0, text.length() - 2));
        }
        return isPrimitive(text) || text.equals("str") || Character.isUpperCase(text.charAt(0));
    }

    private static boolean isPrimitive(String type) {
        return type.equals("int") || type.equals("str") || type.equals("bool") ||
               type.equals("float") || type.equals("void") || type.equals("string");
    }

    private static boolean isIdentifier(String text) {
        return text.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    private static String normalizeType(String type) {
        if (type.endsWith("[]")) {
            return normalizeType(type.substring(0, type.length() - 2)) + "[]";
        }
        return type.equals("string") ? "str" : type;
    }

    private static String elementType(String arrayType) {
        if (!arrayType.endsWith("[]")) {
            throw new RuntimeException("Not an array type: " + arrayType);
        }
        return arrayType.substring(0, arrayType.length() - 2);
    }

    private static boolean looksLikeCall(String text) {
        int lp = text.indexOf('(');
        return lp > 0 && isIdentifier(text.substring(0, lp).trim());
    }

    private static MemberAccess splitMemberAccess(String text) {
        boolean inString = false;
        int parenDepth = 0;
        int bracketDepth = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (inString) continue;
            if (c == ')') parenDepth++;
            else if (c == '(') parenDepth--;
            else if (c == ']') bracketDepth++;
            else if (c == '[') bracketDepth--;
            else if (c == '.' && parenDepth == 0 && bracketDepth == 0) {
                String receiver = text.substring(0, i).trim();
                String member = text.substring(i + 1).trim();
                if (!receiver.isEmpty() && isIdentifier(member)) {
                    return new MemberAccess(receiver, member);
                }
            }
        }
        return null;
    }

    private static IndexAccess splitIndexAccess(String text) {
        if (!text.endsWith("]")) {
            return null;
        }
        boolean inString = false;
        int depth = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (inString) continue;
            if (c == ']') depth++;
            else if (c == '[') {
                depth--;
                if (depth == 0) {
                    String arrayName = text.substring(0, i).trim();
                    String indexExpr = text.substring(i + 1, text.length() - 1).trim();
                    if (!arrayName.isEmpty() && !indexExpr.isEmpty()) {
                        return new IndexAccess(arrayName, indexExpr);
                    }
                }
            }
        }
        return null;
    }

    private static int findTopLevelAssign(String text) {
        boolean inString = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString && c == '=' &&
                (i + 1 >= text.length() || text.charAt(i + 1) != '=') &&
                (i == 0 || (text.charAt(i - 1) != '>' && text.charAt(i - 1) != '<' && text.charAt(i - 1) != '!' && text.charAt(i - 1) != '='))) {
                return i;
            }
        }
        return -1;
    }

    private static OperatorMatch findTopLevelOperator(String text, String[] operators) {
        boolean inString = false;
        int depth = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (inString) continue;
            if (c == ')') depth++;
            else if (c == '(') depth--;
            if (depth == 0) {
                for (String operator : operators) {
                    int start = i - operator.length() + 1;
                    if (start < 0) {
                        continue;
                    }
                    if (text.startsWith(operator, start)) {
                        if ((operator.equals("+") || operator.equals("-")) && start == 0) {
                            continue;
                        }
                        return new OperatorMatch(start, operator);
                    }
                }
            }
        }
        return null;
    }

    private static boolean isWrappedInParens(String text) {
        if (!text.startsWith("(") || !text.endsWith(")")) {
            return false;
        }
        boolean inString = false;
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (inString) {
                continue;
            }
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0 && i < text.length() - 1) {
                    return false;
                }
            }
        }
        return depth == 0;
    }

    private static boolean hasTopLevelComma(String text) {
        boolean inString = false;
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (inString) {
                continue;
            }
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ',' && depth == 0) return true;
        }
        return false;
    }

    private static FunctionDef parseFunctionHeader(String header, int line) {
        String body = header.substring(3, header.length() - 1).trim();
        int lp = body.indexOf('(');
        int rp = body.lastIndexOf(')');
        if (lp < 0 || rp < lp) {
            throw error(line, "Expected fn name(params) returnType:");
        }
        String name = body.substring(0, lp).trim();
        String paramsText = body.substring(lp + 1, rp).trim();
        String returnType = body.substring(rp + 1).trim();
        if (returnType.isEmpty()) {
            returnType = "void";
        }
        FunctionDef fn = new FunctionDef(name, normalizeType(returnType));
        if (!paramsText.isEmpty()) {
            for (String paramText : splitArgs(paramsText)) {
                String[] parts = paramText.trim().split("\\s+");
                if (parts.length != 2) {
                    throw error(line, "Expected parameter: name type");
                }
                fn.params.add(new ParameterDef(parts[0], normalizeType(parts[1])));
            }
        }
        return fn;
    }

    private static MethodDef parseMethodHeader(String header, int line) {
        if (header.contains("(")) {
            int lp = header.indexOf('(');
            int rp = header.lastIndexOf(')');
            if (rp < lp) {
                throw error(line, "Expected methodName(params) returnType:");
            }
            String name = header.substring(0, lp).trim();
            String paramsText = header.substring(lp + 1, rp).trim();
            String returnType = header.substring(rp + 1).trim();
            if (returnType.isEmpty()) {
                returnType = "void";
            }
            MethodDef method = new MethodDef(name, normalizeType(returnType));
            if (!paramsText.isEmpty()) {
                for (String paramText : splitArgs(paramsText)) {
                    String[] parts = paramText.trim().split("\\s+");
                    if (parts.length != 2) {
                        throw error(line, "Expected parameter: name type");
                    }
                    method.params.add(new ParameterDef(parts[0], normalizeType(parts[1])));
                }
            }
            return method;
        }

        String methodName = header;
        String returnType = "void";
        String[] parts = header.split("\\s+");
        if (parts.length == 2) {
            methodName = parts[0];
            returnType = normalizeType(parts[1]);
        } else if (parts.length > 2) {
            throw error(line, "Expected methodName:, methodName returnType:, or methodName(params) returnType:");
        }
        return new MethodDef(methodName, returnType);
    }

    private static String functionSignature(FunctionDef fn) {
        List<String> params = new ArrayList<>();
        for (ParameterDef param : fn.params) {
            params.add(cType(param.type) + " " + param.name);
        }
        return cType(fn.returnType) + " " + fn.name + "(" + String.join(", ", params) + ")";
    }

    private static String methodSignature(String typeName, MethodDef method) {
        List<String> params = new ArrayList<>();
        params.add(typeName + "* self");
        for (ParameterDef param : method.params) {
            params.add(cType(param.type) + " " + param.name);
        }
        return cType(method.returnType) + " " + typeName + "_" + method.name + "(" + String.join(", ", params) + ")";
    }

    private static List<String> splitArgs(String text) {
        List<String> args = new ArrayList<>();
        int start = 0;
        boolean inString = false;
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (!inString && c == '(') {
                depth++;
            } else if (!inString && c == ')') {
                depth--;
            } else if (!inString && depth == 0 && c == ',') {
                args.add(text.substring(start, i).trim());
                start = i + 1;
            }
        }
        args.add(text.substring(start).trim());
        return args;
    }

    private static String cType(String type) {
        type = normalizeType(type);
        switch (type) {
            case "int": return "int";
            case "str": return "char*";
            case "bool": return "bool";
            case "float": return "double";
            case "void": return "void";
            default: return type;
        }
    }

    private static String formatForType(String type) {
        switch (normalizeType(type)) {
            case "int": return "%d";
            case "float": return "%g";
            case "bool": return "%s";
            default: return "%s";
        }
    }

    private static String escape(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static RuntimeException error(int line, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(errorFile).append(":").append(line).append("\n");
        if (line > 0 && line <= errorLines.length) {
            String code = errorLines[line - 1];
            sb.append("    ").append(code).append("\n");
            sb.append("    ^").append("\n");
        }
        sb.append("Error: ").append(message);
        return new RuntimeException(sb.toString());
    }

    private static String spaces(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    private static class Line {
        final int indent;
        final String text;
        Line(int indent, String text) {
            this.indent = indent;
            this.text = text;
        }
    }

    private static class TypeDef {
        final String name;
        boolean explicit = false;
        final Map<String, String> fields = new LinkedHashMap<>();
        final Map<String, MethodDef> methods = new LinkedHashMap<>();
        TypeDef(String name) {
            this.name = name;
        }
    }

    private static class MethodDef {
        final String name;
        final String returnType;
        final List<ParameterDef> params = new ArrayList<>();
        final List<Stmt> body = new ArrayList<>();
        MethodDef(String name, String returnType) {
            this.name = name;
            this.returnType = returnType;
        }
    }

    private static class FunctionDef {
        final String name;
        final String returnType;
        final List<ParameterDef> params = new ArrayList<>();
        final List<Stmt> body = new ArrayList<>();
        FunctionDef(String name, String returnType) {
            this.name = name;
            this.returnType = returnType;
        }
    }

    private static class ParameterDef {
        final String name;
        final String type;
        ParameterDef(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    private interface Stmt {}
    private interface Expr {}

    private static class VarDeclStmt implements Stmt {
        final String type;
        final String name;
        final Expr init;
        VarDeclStmt(String type, String name, Expr init) {
            this.type = type;
            this.name = name;
            this.init = init;
        }
    }

    private static class ArrayDeclStmt implements Stmt {
        final String type;
        final String name;
        final List<Expr> elements = new ArrayList<>();
        ArrayDeclStmt(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    private static class AssignStmt implements Stmt {
        final String target;
        final Expr value;
        AssignStmt(String target, Expr value) {
            this.target = target;
            this.value = value;
        }
    }

    private static class ExprStmt implements Stmt {
        final Expr expr;
        ExprStmt(Expr expr) {
            this.expr = expr;
        }
    }

    private static class ReturnStmt implements Stmt {
        final Expr expr;
        ReturnStmt(Expr expr) {
            this.expr = expr;
        }
    }

    private static class IfStmt implements Stmt {
        final Expr condition;
        final List<Stmt> thenBranch = new ArrayList<>();
        final List<Stmt> elseBranch = new ArrayList<>();
        IfStmt(Expr condition) {
            this.condition = condition;
        }
    }

    private static class WhileStmt implements Stmt {
        final Expr condition;
        final List<Stmt> body = new ArrayList<>();
        WhileStmt(Expr condition) {
            this.condition = condition;
        }
    }

    private static class ForStmt implements Stmt {
        final String itemName;
        final String arrayName;
        final List<Stmt> body = new ArrayList<>();
        ForStmt(String itemName, String arrayName) {
            this.itemName = itemName;
            this.arrayName = arrayName;
        }
    }

    private static class MethodAttachStmt implements Stmt {
        MethodAttachStmt(String objectName, String methodName) {}
    }

    private static class StringExpr implements Expr {
        final String value;
        StringExpr(String value) {
            this.value = value;
        }
    }

    private static class IntExpr implements Expr {
        final String value;
        IntExpr(String value) {
            this.value = value;
        }
    }

    private static class FloatExpr implements Expr {
        final String value;
        FloatExpr(String value) {
            this.value = value;
        }
    }

    private static class BoolExpr implements Expr {
        final boolean value;
        BoolExpr(boolean value) {
            this.value = value;
        }
    }

    private static class NameExpr implements Expr {
        final String name;
        NameExpr(String name) {
            this.name = name;
        }
    }

    private static class BinaryExpr implements Expr {
        final Expr left;
        final String operator;
        final Expr right;
        BinaryExpr(Expr left, String operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    private static class ConstructExpr implements Expr {
        final String typeName;
        final List<Expr> args;
        ConstructExpr(String typeName, List<Expr> args) {
            this.typeName = typeName;
            this.args = args;
        }
    }

    private static class TupleExpr implements Expr {
        final List<Expr> args;
        TupleExpr(List<Expr> args) {
            this.args = args;
        }
    }

    private static class OperatorMatch {
        final int index;
        final String operator;
        OperatorMatch(int index, String operator) {
            this.index = index;
            this.operator = operator;
        }
    }

    private static class MemberAccess {
        final String receiver;
        final String member;
        MemberAccess(String receiver, String member) {
            this.receiver = receiver;
            this.member = member;
        }
    }

    private static class IndexAccess {
        final String arrayName;
        final String indexExpr;
        IndexAccess(String arrayName, String indexExpr) {
            this.arrayName = arrayName;
            this.indexExpr = indexExpr;
        }
    }

    private static class CallExpr implements Expr {
        final String callee;
        final List<Expr> args;
        CallExpr(String callee, List<Expr> args) {
            this.callee = callee;
            this.args = args;
        }
    }
}
