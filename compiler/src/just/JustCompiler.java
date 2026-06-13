package just;

import just.ast.ASTNodes.Program;
import just.codegen.CCodeGenerator;
import just.lexer.Lexer;
import just.parser.Parser;
import just.semantic.SemanticAnalyzer;
import just.simple.SimpleJustCompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JustCompiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java just.JustCompiler <source.just>");
            System.err.println("Example: java just.JustCompiler examples/hello.just");
            System.exit(1);
        }

        String sourceFile = args[0];
        String outputFile = sourceFile.replace(".just", "");

        try {
            String source = new String(Files.readAllBytes(Paths.get(sourceFile)));

            if (SimpleJustCompiler.looksLikeSimpleSyntax(source)) {
                System.out.println("Just simple syntax compiler v0.1");
                System.out.println("Compiling: " + sourceFile);
                new SimpleJustCompiler(source, outputFile).compile();
                System.out.println("Generated files:");
                System.out.println("  - " + outputFile + ".h");
                System.out.println("  - " + outputFile + ".c");
                return;
            }

            System.out.println("Just compiler v0.1");
            System.out.println("Compiling: " + sourceFile);
            System.out.println();

            System.out.println("=== 1. Lexing ===");
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.scanTokens();
            System.out.println("Generated " + tokens.size() + " tokens");
            System.out.println();

            System.out.println("=== 2. Parsing ===");
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            System.out.println("Parsed " + program.classes.size() + " classes");
            System.out.println();

            System.out.println("=== 3. Semantic analysis ===");
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(program);
            System.out.println();

            System.out.println("=== 4. C code generation ===");
            CCodeGenerator generator = new CCodeGenerator(analyzer.getClasses(), outputFile);
            generator.generate(program);
            System.out.println();

            System.out.println("Generated files:");
            System.out.println("  - " + outputFile + ".h");
            System.out.println("  - " + outputFile + ".c");
        } catch (IOException e) {
            System.err.println("Error: unable to read file " + sourceFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println();
            System.err.println("Compilation failed:");
            System.err.println(e.getMessage());
            if (args.length > 1 && args[1].equals("--debug")) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
