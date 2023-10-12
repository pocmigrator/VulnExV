package demo.example;


import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.beans.Expression;

public class DataFlowAnalysisVisitor extends Java8BaseVisitor<Void> {
    private boolean insideVulnerableMethod = false;

    @Override
    public Void visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        // 判断是否进入了漏洞方法
        String methodName = ctx.methodHeader().methodDeclarator().Identifier().getText();
        if (methodName.equals("vulnerableMethod1") || methodName.equals("vulnerableMethod2")
                || methodName.equals("vulnerableMethod3") || methodName.equals("vulnerableMethod4")) {
            insideVulnerableMethod = true;
        } else {
            insideVulnerableMethod = false;
        }
        return super.visitMethodDeclaration(ctx);
    }

    @Override
    public Void visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        // 检查方法调用是否是readValue方法
        if (insideVulnerableMethod && ctx.Identifier().getText().equals("readValue")) {
            ParserRuleContext arg = (ParserRuleContext) ctx.argumentList().expression(0);
            System.out.println(arg.getText());

            // 检查参数来源，这里只是简化的逻辑
            if (arg instanceof Java8Parser.ExpressionStatementContext) {
                System.out.println("Method: " + getMethodName(ctx));
                System.out.println("Parameter " + arg.getText() + " is directly passed as an argument.");
            } else if (arg instanceof Java8Parser.MethodInvocationContext) {
                System.out.println("Method: " + getMethodName(ctx));
                System.out.println("Parameter " + arg.getText() + " comes from another method call.");
            } else if (arg instanceof Java8Parser.ClassInstanceCreationExpressionContext) {
                System.out.println("Method: " + getMethodName(ctx));
                System.out.println("Parameter " + arg.getText() + " is created internally.");
            }
        }
        return super.visitMethodInvocation(ctx);
    }

    // 获取方法名，用于输出
    private String getMethodName(ParserRuleContext ctx) {
        return ctx.getParent().getParent().getChild(0).getText();
    }
}