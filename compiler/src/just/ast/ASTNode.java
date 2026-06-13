package just.ast;

/**
 * AST 节点的基类
 */
public abstract class ASTNode {
    public int line;
    public int column;

    public ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);
}
