package just.semantic;
import just.ast.ASTNodes.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 符号 - 表示一个变量、方法或类
 */
public class Symbol {
    public enum Kind {
        VARIABLE,    // 局部变量或参数
        FIELD,       // 类字段
        METHOD,      // 方法
        CLASS        // 类
    }

    public final String name;
    public final Kind kind;
    public final String type;      // 类型名称
    public final boolean isArray;
    public final boolean isPublic;
    public final int line;
    public final int column;

    public Symbol(String name, Kind kind, String type, boolean isArray,
                  boolean isPublic, int line, int column) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.isArray = isArray;
        this.isPublic = isPublic;
        this.line = line;
        this.column = column;
    }

    public String getFullType() {
        return type + (isArray ? "[]" : "");
    }

    @Override
    public String toString() {
        return String.format("Symbol(%s, %s, %s, %d:%d)",
            name, kind, getFullType(), line, column);
    }
}

/**
 * 符号表 - 管理作用域中的符号
 */
class SymbolTable {
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final String scopeName;

    public SymbolTable(String scopeName, SymbolTable parent) {
        this.scopeName = scopeName;
        this.parent = parent;
    }

    public void define(Symbol symbol) {
        if (symbols.containsKey(symbol.name)) {
            Symbol existing = symbols.get(symbol.name);
            throw new RuntimeException(
                String.format("重复定义: '%s' 在 %d:%d 已经在 %d:%d 定义过",
                    symbol.name, symbol.line, symbol.column,
                    existing.line, existing.column)
            );
        }
        symbols.put(symbol.name, symbol);
    }

    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        return null;
    }

    public boolean isDefined(String name) {
        return symbols.containsKey(name);
    }

    public SymbolTable getParent() {
        return parent;
    }

    public String getScopeName() {
        return scopeName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SymbolTable(").append(scopeName).append("):\n");
        for (Symbol symbol : symbols.values()) {
            sb.append("  ").append(symbol).append("\n");
        }
        return sb.toString();
    }
}
