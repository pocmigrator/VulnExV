package demo.example;

import antlr.Java8Lexer;
import antlr.Java8Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;

public class ParameterSourceAnalysis {
    public static void main(String[] args) {
        String filePath = "/Users//IdeaProjects/LLMPocMigration/testdata/Demo1.java";
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromFileName(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommonTokenStream commonTokenStream = new CommonTokenStream(new Java8Lexer(inputStream));
        Java8Parser parser = new Java8Parser(commonTokenStream);
        ParseTree parseTree = parser.compilationUnit();

        // 在AST上执行数据流分析
        DataFlowAnalysisVisitor visitor = new DataFlowAnalysisVisitor();
        visitor.visit(parseTree);
    }
}