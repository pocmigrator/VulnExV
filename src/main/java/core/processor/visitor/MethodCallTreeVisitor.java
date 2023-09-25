package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Lexer;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.entity.analysis.CallMethod;
import core.entity.analysis.PClass;
import core.entity.analysis.PMethod;
import core.entity.analysis.PSourceFile;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
public class MethodCallTreeVisitor extends Java8BaseVisitor<RuleNode> {

    /**
     * input
     **/
    private PSourceFile pSourceFile;
    private PClass pClass;
    private PMethod pMethod;

    /**
     * output
     **/
    private List<CallMethod> callMethods = Lists.newArrayList();


    public MethodCallTreeVisitor(PSourceFile pSourceFile, PClass pClass, PMethod pMethod) {
        this.pSourceFile = pSourceFile;
        this.pClass = pClass;
        this.pMethod = pMethod;
    }

    @Override
    public RuleNode visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethodClassCreation(ctx, callMethod);
        callMethods.add(callMethod);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethodClassCreation(ctx, callMethod);
        callMethods.add(callMethod);

        return visitChildren(ctx);
    }


    @Override
    public RuleNode visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethodClassCreation(ctx, callMethod);
        callMethods.add(callMethod);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethod(ctx, callMethod);
        callMethods.add(callMethod);

        return visitChildren(ctx);
    }


    @Override
    public RuleNode visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethod(ctx, callMethod);
        callMethods.add(callMethod);
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        CallMethod callMethod = new CallMethod();
        fillCallMethod(ctx, callMethod);
        callMethods.add(callMethod);
        return visitChildren(ctx);
    }

    private void fillCallMethodClassCreation(ParserRuleContext ctx, CallMethod callMethod) {
        String className = StringUtils.EMPTY;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);

            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    className = terminalNode.getText();
                    break;
                }
            }
        }

        callMethod.setClassName(className);
        callMethod.setMethodName(className);
        callMethod.setParamList(Lists.newArrayList());
    }

    private void fillCallMethod(ParserRuleContext ctx, CallMethod callMethod) {
        // 1. acquire typeName（a.func1 -> a） methodName
        String className = StringUtils.EMPTY;
        String typeName = StringUtils.EMPTY;
        String methodName = StringUtils.EMPTY;
        ParserRuleContext argumentListCtx = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof RuleContext) {
                RuleContext ruleContext = (RuleContext) child;
                if (ruleContext.getRuleIndex() == Java8Parser.RULE_argumentList) {
                    argumentListCtx = (ParserRuleContext) ruleContext;
                }
            }
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    methodName = terminalNode.getText();
                }
            }
        }

        ParseTree firstChild = ctx.getChild(0);
        if (firstChild instanceof RuleContext) {
            RuleContext firstChildCtx = (RuleContext) firstChild;
            // methodName '(' argumentList? ')'
            if (firstChildCtx.getRuleIndex() == Java8Parser.RULE_methodName) {
                typeName = pClass.getClassName();
                methodName = firstChildCtx.getText();
            } else {
                // typeName '.' typeArguments? Identifier '(' argumentList? ')'
                // typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
                if (firstChildCtx.getRuleIndex() == Java8Parser.RULE_typeName) {
                    typeName = firstChildCtx.getText();
                }
                // expressionName '.' typeArguments? Identifier '(' argumentList? ')'
                if (firstChildCtx.getRuleIndex() == Java8Parser.RULE_expressionName) {
                    typeName = firstChildCtx.getText();
                }
                // primary '.' typeArguments? Identifier '(' argumentList? ')'
                if (firstChildCtx.getRuleIndex() == Java8Parser.RULE_primary) {
                    typeName = firstChildCtx.getText();
                }
            }
        }
        // 'super' '.' typeArguments? Identifier '(' argumentList? ')'
        // '.' typeArguments? Identifier '(' argumentList? ')'
        else {
            typeName = pClass.getClassName();
        }

        // 2. match by: class fields
        className = matchClassNameByDeclarations(typeName, pClass.getFieldDeclarations());
        // 3. match by: method vars
        if (StringUtils.isBlank(className)) {
            className = matchClassNameByDeclarations(typeName, pMethod.getLocalVariableDeclarations());
        }

        // match by method declarator
        if (StringUtils.isBlank(className)) {
            className = matchClassNameByDeclarator(typeName, (ParserRuleContext) pMethod.getTree());
        }

        // match by unannType variableDeclaratorId
        if (StringUtils.isBlank(className)) {
            className = matchClassNameByTypeVarId(typeName, (ParserRuleContext) pMethod.getTree());
        }

        // import static
        if (StringUtils.isBlank(className)) {
            className = matchClassNameByImportStatic(typeName, methodName, pSourceFile.getImportList());
        }

        // static invoke
        if (StringUtils.isBlank(className)) {
            className = matchClassNameByStaticInvoke(typeName, pSourceFile.getImportList());
        }

        if (StringUtils.isBlank(className)) {
            className = pClass.getClassName();
        }

//        System.out.println("+++ methodName: "+methodName +", className: "+className+", caller:"+pMethod.getMethodName()+", callerClass:"+pClass.getClassName());

        callMethod.setClassName(preprocessName(className));
        callMethod.setMethodName(methodName);
        if (argumentListCtx == null) {
            callMethod.setParamList(Lists.newArrayList());
        } else {
            List<ParseTree> paramList = Lists.newArrayList();
            for (int i = 0; i < argumentListCtx.getChildCount(); i++) {
                if (argumentListCtx.getChild(i) instanceof RuleContext) {
                    paramList.add(argumentListCtx.getChild(i));
                }
            }
            callMethod.setParamList(paramList);
        }
    }

    private String matchClassNameByImportStatic(String typeName, String methodName, List<String> importList) {
        // import static a.Cls.func;
        for (String s : importList) {
            String[] split = s.split("\\.");
            if(split.length < 2){continue;}
            String iMethodName = split[split.length - 1].trim();
            if(StringUtils.equals(iMethodName, methodName)){
                String s1 = split[split.length - 2];
                if(Character.isUpperCase(s1.charAt(0))){
                    return s1;
                }
            }
        }
        return null;
    }

    private String matchClassNameByStaticInvoke(String typeName, List<String> importList) {
        // import a.Cls;
        // Cls.func();
        for (String s : importList) {
            String[] split = s.split("\\.");
            String className = split[split.length - 1].trim();
            if (StringUtils.equals(typeName, className)) {
                return className;
            }
        }
        if (StringUtils.isNotBlank(typeName) && Character.isUpperCase(typeName.charAt(0))) {
            return typeName;
        }

        // a.b.Cls.func();
        if (typeName.contains(".")) {
            String[] split = typeName.split("\\.");
            if (split.length > 1) {
                return typeName;
            }
        }

        return null;
    }

    private String matchClassNameByDeclarator(String typeName, ParserRuleContext tree) {
        MethodDeclaratorVisitor methodDeclaratorVisitor = new MethodDeclaratorVisitor();
        methodDeclaratorVisitor.visit(tree);
        List<String> types = methodDeclaratorVisitor.getTypes();
        List<String> varIds = methodDeclaratorVisitor.getVarIds();
        int size = Math.min(types.size(), varIds.size());
        Map<String, String> varTypeMap = Maps.newHashMap();

        for (int i = 0; i < size; i++) {
            varTypeMap.put(varIds.get(i), types.get(i));
        }

        return varTypeMap.getOrDefault(typeName, StringUtils.EMPTY);
    }

    private String preprocessName(String className) {
        if (className.contains("<")) {
            className = className.split("<")[0].trim();
        }
        return className;
    }

    private String matchClassNameByTypeVarId(String typeName, ParserRuleContext ctx) {
        MethodCallVisitor visitor = new MethodCallVisitor();
        visitor.visit(ctx);
        Map<String, String> varTypeMap = visitor.getVarTypeMap();
        return varTypeMap.getOrDefault(typeName, StringUtils.EMPTY);
    }

    /**
     * @param typeName
     * @return
     */
    private String matchClassNameByDeclarations(String typeName, List<String> declarations) {
        if (StringUtils.isBlank(typeName)) {
            return StringUtils.EMPTY;
        }
        for (String declaration : declarations) {
            if (declaration.contains("=")) {
                declaration = declaration.replace("=", " = ");
            }
            List<String> declarationList = Arrays.stream(declaration.trim().split(" "))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (declarationList.contains(typeName)) {
                return declarationList.get(0);
            }
        }
        return StringUtils.EMPTY;
    }

}
