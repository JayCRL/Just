package just.semantic;
import just.ast.ASTNodes.*;

import just.ast.*;
import java.util.*;

/**
 * 泛型系统 - 类型参数推导和单态化
 */
public class GenericSystem {
    private final Map<String, GenericClassInfo> genericClasses = new HashMap<>();
    private final Map<String, MonomorphizedClass> instantiatedClasses = new HashMap<>();

    /**
     * 泛型类信息
     */
    public static class GenericClassInfo {
        public final String name;
        public final List<String> typeParameters;
        public final Map<String, TypeBound> bounds;
        public final ClassDecl template;

        public GenericClassInfo(String name, List<String> typeParameters,
                               Map<String, TypeBound> bounds, ClassDecl template) {
            this.name = name;
            this.typeParameters = typeParameters;
            this.bounds = bounds;
            this.template = template;
        }
    }

    /**
     * 类型约束
     */
    public static class TypeBound {
        public final List<String> interfaces;
        public final String superClass;

        public TypeBound(List<String> interfaces, String superClass) {
            this.interfaces = interfaces;
            this.superClass = superClass;
        }
    }

    /**
     * 单态化的类
     */
    public static class MonomorphizedClass {
        public final String originalName;
        public final Map<String, String> typeArguments;
        public final ClassDecl instantiatedClass;

        public MonomorphizedClass(String originalName,
                                 Map<String, String> typeArguments,
                                 ClassDecl instantiatedClass) {
            this.originalName = originalName;
            this.typeArguments = typeArguments;
            this.instantiatedClass = instantiatedClass;
        }
    }

    /**
     * 注册泛型类
     */
    public void registerGenericClass(String name, List<String> typeParameters,
                                     Map<String, TypeBound> bounds,
                                     ClassDecl template) {
        GenericClassInfo info = new GenericClassInfo(name, typeParameters,
                                                      bounds, template);
        genericClasses.put(name, info);
    }

    /**
     * 实例化泛型类（单态化）
     */
    public ClassDecl instantiate(String genericClassName,
                                 List<String> typeArguments) {
        GenericClassInfo info = genericClasses.get(genericClassName);
        if (info == null) {
            throw new RuntimeException("未定义的泛型类: " + genericClassName);
        }

        if (typeArguments.size() != info.typeParameters.size()) {
            throw new RuntimeException(
                String.format("类型参数数量不匹配: 期望 %d, 实际 %d",
                    info.typeParameters.size(), typeArguments.size()));
        }

        // 检查是否已实例化
        String instantiatedName = makeInstantiatedName(genericClassName, typeArguments);
        if (instantiatedClasses.containsKey(instantiatedName)) {
            return instantiatedClasses.get(instantiatedName).instantiatedClass;
        }

        // 创建类型映射
        Map<String, String> typeMap = new HashMap<>();
        for (int i = 0; i < info.typeParameters.size(); i++) {
            typeMap.put(info.typeParameters.get(i), typeArguments.get(i));
        }

        // 检查类型约束
        checkBounds(info, typeMap);

        // 克隆并替换类型参数
        ClassDecl instantiated = instantiateClass(info.template, typeMap, instantiatedName);

        // 缓存实例
        MonomorphizedClass mono = new MonomorphizedClass(genericClassName,
                                                         typeMap, instantiated);
        instantiatedClasses.put(instantiatedName, mono);

        return instantiated;
    }

    /**
     * 检查类型约束
     */
    private void checkBounds(GenericClassInfo info, Map<String, String> typeMap) {
        for (Map.Entry<String, String> entry : typeMap.entrySet()) {
            String typeParam = entry.getKey();
            String actualType = entry.getValue();

            TypeBound bound = info.bounds.get(typeParam);
            if (bound == null) continue;

            // TODO: 检查 actualType 是否满足约束
        }
    }

    /**
     * 实例化类（替换类型参数）
     */
    private ClassDecl instantiateClass(ClassDecl template,
                                       Map<String, String> typeMap,
                                       String newName) {
        // TODO: 深度克隆并替换所有类型参数
        // 简化版本：直接返回模板
        return template;
    }

    /**
     * 生成实例化后的类名
     */
    private String makeInstantiatedName(String baseName, List<String> typeArgs) {
        StringBuilder sb = new StringBuilder(baseName);
        sb.append("_");
        for (String arg : typeArgs) {
            sb.append(arg.replace("[]", "Array")).append("_");
        }
        return sb.toString();
    }

    /**
     * 类型推导
     */
    public List<String> inferTypeArguments(String genericClassName,
                                          List<String> paramTypes,
                                          List<String> argTypes) {
        GenericClassInfo info = genericClasses.get(genericClassName);
        if (info == null) return null;

        Map<String, String> inferred = new HashMap<>();

        // 简单推导：从参数类型推导
        for (int i = 0; i < paramTypes.size() && i < argTypes.size(); i++) {
            String paramType = paramTypes.get(i);
            String argType = argTypes.get(i);

            if (info.typeParameters.contains(paramType)) {
                inferred.put(paramType, argType);
            }
        }

        // 构建类型参数列表
        List<String> result = new ArrayList<>();
        for (String typeParam : info.typeParameters) {
            String inferredType = inferred.get(typeParam);
            if (inferredType == null) {
                throw new RuntimeException("无法推导类型参数: " + typeParam);
            }
            result.add(inferredType);
        }

        return result;
    }

    public boolean isGenericClass(String name) {
        return genericClasses.containsKey(name);
    }

    public GenericClassInfo getGenericClass(String name) {
        return genericClasses.get(name);
    }

    public Collection<MonomorphizedClass> getInstantiatedClasses() {
        return instantiatedClasses.values();
    }
}
