package just.semantic;
import just.ast.ASTNodes.*;

import just.ast.*;
import java.util.*;

/**
 * 类信息 - 存储类的元数据
 */
public class ClassInfo {
    public final String name;
    public final String superClass;
    public final List<String> interfaces;
    public final Map<String, FieldInfo> fields;
    public final Map<String, MethodInfo> methods;
    public final int line;
    public final int column;

    public ClassInfo(String name, String superClass, List<String> interfaces,
                     int line, int column) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
        this.line = line;
        this.column = column;
    }

    public void addField(FieldInfo field) {
        if (fields.containsKey(field.name)) {
            throw new RuntimeException(
                String.format("类 %s 中字段 '%s' 重复定义", name, field.name)
            );
        }
        fields.put(field.name, field);
    }

    public void addMethod(MethodInfo method) {
        // 允许方法重载（同名不同参数）
        String signature = method.getSignature();
        if (methods.containsKey(signature)) {
            throw new RuntimeException(
                String.format("类 %s 中方法 '%s' 重复定义", name, signature)
            );
        }
        methods.put(signature, method);
    }

    public FieldInfo getField(String name) {
        return fields.get(name);
    }

    public MethodInfo getMethod(String name, List<String> paramTypes) {
        String signature = MethodInfo.makeSignature(name, paramTypes);
        return methods.get(signature);
    }

    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    public boolean hasMethod(String name) {
        for (String sig : methods.keySet()) {
            if (sig.startsWith(name + "(")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字段信息
     */
    public static class FieldInfo {
        public final String name;
        public final String type;
        public final boolean isArray;
        public final boolean isPublic;
        public final int line;
        public final int column;

        public FieldInfo(String name, String type, boolean isArray,
                         boolean isPublic, int line, int column) {
            this.name = name;
            this.type = type;
            this.isArray = isArray;
            this.isPublic = isPublic;
            this.line = line;
            this.column = column;
        }

        public String getFullType() {
            return type + (isArray ? "[]" : "");
        }
    }

    /**
     * 方法信息
     */
    public static class MethodInfo {
        public final String name;
        public final String returnType;
        public final boolean returnIsArray;
        public final List<String> paramTypes;
        public final List<String> paramNames;
        public final boolean isPublic;
        public final boolean isConstructor;
        public final boolean isOperator;
        public final int line;
        public final int column;

        public MethodInfo(String name, String returnType, boolean returnIsArray,
                          List<String> paramTypes, List<String> paramNames,
                          boolean isPublic, boolean isConstructor, boolean isOperator,
                          int line, int column) {
            this.name = name;
            this.returnType = returnType;
            this.returnIsArray = returnIsArray;
            this.paramTypes = paramTypes;
            this.paramNames = paramNames;
            this.isPublic = isPublic;
            this.isConstructor = isConstructor;
            this.isOperator = isOperator;
            this.line = line;
            this.column = column;
        }

        public String getSignature() {
            return makeSignature(name, paramTypes);
        }

        public static String makeSignature(String name, List<String> paramTypes) {
            StringBuilder sb = new StringBuilder(name);
            sb.append("(");
            for (int i = 0; i < paramTypes.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(paramTypes.get(i));
            }
            sb.append(")");
            return sb.toString();
        }

        public String getFullReturnType() {
            return returnType + (returnIsArray ? "[]" : "");
        }
    }
}
