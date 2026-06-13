package just;

import just.lexer.Lexer;
import just.parser.Parser;
import just.ast.ASTNodes.Program;
import just.semantic.SemanticAnalyzer;
import just.codegen.CCodeGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Just 编译器主程序
 */
public class JustCompiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("用法: java just.JustCompiler <source.just>");
            System.err.println("示例: java just.JustCompiler examples/hello.just");
            System.exit(1);
        }

        String sourceFile = args[0];
        String outputFile = sourceFile.replace(".just", "");

        try {
            System.out.println("Just 编译器 v0.1");
            System.out.println("正在编译: " + sourceFile);
            System.out.println();

            // 读取源代码
            String source = new String(Files.readAllBytes(Paths.get(sourceFile)));

            // 词法分析
            System.out.println("=== 1. 词法分析 ===");
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            System.out.println("✓ 生成 " + tokens.size() + " 个 Token");
            System.out.println();

            // 语法分析
            System.out.println("=== 2. 语法分析 ===");
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            System.out.println("✓ 解析成功！找到 " + program.classes.size() + " 个类");
            System.out.println();

            // 语义分析
            System.out.println("=== 3. 语义分析 ===");
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(program);
            System.out.println();

            // C 代码生成
            System.out.println("=== 4. C 代码生成 ===");
            CCodeGenerator generator = new CCodeGenerator(
                analyzer.getClasses(),
                outputFile
            );
            generator.generate(program);
            System.out.println();

            System.out.println("=== 编译成功！===");
            System.out.println("生成文件:");
            System.out.println("  - " + outputFile + ".h");
            System.out.println("  - " + outputFile + ".c");
            System.out.println();
            System.out.println("下一步：使用 GCC 编译 C 代码");
            System.out.println("  gcc -o " + outputFile + " " + outputFile + ".c runtime/runtime.c -lgc");

        } catch (IOException e) {
            System.err.println("错误: 无法读取文件 " + sourceFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n编译失败:");
            System.err.println(e.getMessage());
            if (args.length > 1 && args[1].equals("--debug")) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
