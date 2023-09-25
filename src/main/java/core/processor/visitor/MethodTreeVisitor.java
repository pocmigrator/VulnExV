package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Lexer;
import antlr.Java8Parser;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import core.entity.analysis.MethodParam;
import core.entity.analysis.PClass;
import core.entity.analysis.PMethod;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class MethodTreeVisitor extends Java8BaseVisitor<RuleNode> {
    private PClass pClass;
    private List<PMethod> pMethodList = Lists.newArrayList();

    public MethodTreeVisitor(PClass pClass) {
        this.pClass = pClass;
    }

    @Override
    public RuleNode visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        ParseTree identifier = null;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    identifier = terminalNode;
                }
            }
        }
        String className = identifier == null ? "" : identifier.getText();
        List<String> fieldDeclarations = Lists.newArrayList();
        if (StringUtils.equals(className, pClass.getClassName())) {
            ParserRuleContext classBodyCtx;
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree childCtx = ctx.getChild(i);
                if (childCtx instanceof RuleContext && ((RuleContext) childCtx).getRuleIndex() == Java8Parser.RULE_classBody) {
                    classBodyCtx = (ParserRuleContext) childCtx;
                    List<ParseTree> classBodyDeclarationCtxList = Lists.newArrayList();
                    for (int j = 0; j < classBodyCtx.getChildCount(); j++) {
                        ParseTree classBodyChildCtx = classBodyCtx.getChild(j);
                        if (classBodyChildCtx instanceof RuleContext && ((RuleContext) classBodyChildCtx).getRuleIndex() == Java8Parser.RULE_classBodyDeclaration) {
                            classBodyDeclarationCtxList.add(classBodyChildCtx);
                        }
                    }

                    for (ParseTree classBodyDeclarationCtx : classBodyDeclarationCtxList) {
                        ParserRuleContext child = (ParserRuleContext) classBodyDeclarationCtx.getChild(0);
                        if (child.getRuleIndex() == Java8Parser.RULE_constructorDeclaration) {
                            addPMethodByConstructorDeclaration(child);
                        }

                        if (child.getRuleIndex() == Java8Parser.RULE_classMemberDeclaration) {
                            addPMethodByClassMemberDeclaration(fieldDeclarations, child);
                        }
                    }
                }
            }
        }
        pClass.setFieldDeclarations(fieldDeclarations);
        return visitChildren(ctx);
    }

    private void addPMethodByConstructorDeclaration(ParserRuleContext child) {
        for (int j = 0; j < child.getChildCount(); j++) {
            ParseTree childChild = child.getChild(j);
            boolean isRuleContext = childChild instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext node = (ParserRuleContext) childChild;
            ParserRuleContext simpleTypeNameCtx = null;
            ParserRuleContext formalParameterListCtx = null;
            if (node.getRuleIndex() == Java8Parser.RULE_constructorDeclarator) {
                for (int k = 0; k < node.getChildCount(); k++) {
                    ParseTree childChildChild = node.getChild(k);
                    boolean isChildRuleContext = childChildChild instanceof RuleContext;
                    if (!isChildRuleContext) {
                        continue;
                    }
                    ParserRuleContext childRuleContext = (ParserRuleContext) childChildChild;
                    if (childRuleContext.getRuleIndex() == Java8Parser.RULE_simpleTypeName) {
                        simpleTypeNameCtx = childRuleContext;
                    }

                    if (childRuleContext.getRuleIndex() == Java8Parser.RULE_formalParameterList) {
                        formalParameterListCtx = childRuleContext;
                    }

                    PMethod pMethod = PMethod.builder()
                            .pClassCid(pClass.getCid())
                            .methodName(simpleTypeNameCtx == null ? "" : simpleTypeNameCtx.getText())
                            .className(pClass.getClassName())
                            .methodModifier("public")
                            .params(buildMethodParams(formalParameterListCtx))
                            .startIndex(childRuleContext.getStart().getStartIndex())
                            .endIndex(childRuleContext.getStop().getStopIndex())
                            .tree(child)
                            .build();
                    this.pMethodList.add(pMethod);
                }
            }
        }
    }

    private void addPMethodByClassMemberDeclaration(List<String> fieldDeclarations, ParserRuleContext child) {
        // ;
        if (child.getChild(0) instanceof TerminalNode) {
            return;
        }

        ParserRuleContext childChild = (ParserRuleContext) child.getChild(0);
        if (childChild.getRuleIndex() == Java8Parser.RULE_fieldDeclaration) {
            ParserRuleContext fieldDeclarationCtx = childChild;
            ParserRuleContext unannTypeCtx = null;
            ParserRuleContext variableDeclaratorListCtx = null;
            StringBuilder fieldDeclarationsStr = new StringBuilder();
            for (int j = 0; j < fieldDeclarationCtx.getChildCount(); j++) {
                boolean isRuleContext = fieldDeclarationCtx.getChild(j) instanceof RuleContext;
                if (!isRuleContext) {
                    continue;
                }
                ParserRuleContext childRuleContext = (ParserRuleContext) fieldDeclarationCtx.getChild(j);
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                    unannTypeCtx = childRuleContext;
                }
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorList) {
                    variableDeclaratorListCtx = childRuleContext;
                }
            }

            if (unannTypeCtx != null) {
                fieldDeclarationsStr.append(unannTypeCtx.getText()).append(" ");
            }

            if (variableDeclaratorListCtx != null) {
                List<String> variableDeclaratorList = Lists.newArrayList();
                for (int j = 0; j < variableDeclaratorListCtx.getChildCount(); j++) {
                    boolean isRuleContext = variableDeclaratorListCtx.getChild(j) instanceof RuleContext;
                    if (!isRuleContext) {
                        continue;
                    }
                    ParserRuleContext childRuleContext = (ParserRuleContext) variableDeclaratorListCtx.getChild(j);
                    if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclarator) {
                        variableDeclaratorList.add(childRuleContext.getText());
                    }
                }
                String join = Joiner.on(",").join(variableDeclaratorList);
                fieldDeclarationsStr.append(join);
            }
            fieldDeclarations.add(fieldDeclarationsStr.toString());
        }

        if (childChild.getRuleIndex() == Java8Parser.RULE_methodDeclaration) {
            String methodModifier = parseMethodModifier(childChild);
            List<MethodParam> methodParams = parseMethodParams(childChild);
            PMethod pMethod = PMethod.builder()
                    .pClassCid(pClass.getCid())
                    .methodName(parseMethodName(childChild))
                    .className(pClass.getClassName())
                    .methodModifier(methodModifier)
                    .params(methodParams)
                    .startIndex(childChild.getStart().getStartIndex())
                    .endIndex(childChild.getStop().getStopIndex())
                    .tree(childChild)
                    .build();
            this.pMethodList.add(pMethod);
        }
    }

    private String parseMethodModifier(ParserRuleContext ctx) {
        List<ParserRuleContext> methodModifierCtxList = Lists.newArrayList();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext node = (ParserRuleContext) child;

            if (node.getRuleIndex() == Java8Parser.RULE_methodModifier) {
                methodModifierCtxList.add(node);
            }
        }
        List<String> modifiers = methodModifierCtxList.stream()
                .map(RuleContext::getText)
                .collect(Collectors.toList());

        return Joiner.on(" ").join(modifiers);
    }

    private String parseMethodName(ParserRuleContext ctx) {
        String methodName = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            boolean isRuleContext = child instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            RuleContext node = (RuleContext) child;

            if (node.getRuleIndex() == Java8Parser.RULE_methodModifier) {
                boolean existAnnotation = isExistAnnotation(node);
                if (existAnnotation) {
                    continue;
                }

//                if (!StringUtils.equalsAnyIgnoreCase(node.getText(), "public")) {
//                    methodName = "";
//                }
            }

            if (node.getRuleIndex() != Java8Parser.RULE_methodHeader) {
                continue;
            }

            for (int j = 0; j < node.getChildCount(); j++) {
                ParseTree methodHeaderChild = node.getChild(j);
                boolean isMethodHeaderChildRuleContext = methodHeaderChild instanceof RuleContext;
                if (!isMethodHeaderChildRuleContext) {
                    continue;
                }
                RuleContext methodHeaderChildNode = (RuleContext) methodHeaderChild;

                if (methodHeaderChildNode.getRuleIndex() == Java8Parser.RULE_methodDeclarator) {
                    for (int k = 0; k < methodHeaderChildNode.getChildCount(); k++) {
                        ParseTree child1 = methodHeaderChildNode.getChild(k);
                        if (child1 instanceof TerminalNode) {
                            TerminalNode terminalNode = (TerminalNode) child1;
                            if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                                methodName = terminalNode.getText();
                            }
                        }

                    }
                }
            }
        }
        return methodName;
    }

    private boolean isExistAnnotation(RuleContext node) {
        boolean existAnnotation = false;
        for (int j = 0; j < node.getChildCount(); j++) {
            boolean isChildRuleContext = node.getChild(j) instanceof RuleContext;
            if (!isChildRuleContext) {
                continue;
            }
            RuleContext methodModifierNode = (RuleContext) node.getChild(j);
            if (methodModifierNode.getRuleIndex() == Java8Parser.RULE_annotation) {
                existAnnotation = true;
            }
        }
        return existAnnotation;
    }

    private List<MethodParam> parseMethodParams(ParserRuleContext methodCtx) {

        ParserRuleContext methodHeaderCtx = null;
        for (int i = 0; i < methodCtx.getChildCount(); i++) {
            boolean isRuleContext = methodCtx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) methodCtx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodHeader) {
                methodHeaderCtx = childRuleContext;
            }
        }
        if (methodHeaderCtx == null) {
            return Lists.newArrayList();
        }

        ParserRuleContext methodDeclaratorCtx = null;
        for (int i = 0; i < methodHeaderCtx.getChildCount(); i++) {
            boolean isRuleContext = methodHeaderCtx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) methodHeaderCtx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_methodDeclarator) {
                methodDeclaratorCtx = childRuleContext;
            }
        }
        if (methodDeclaratorCtx == null) {
            return Lists.newArrayList();
        }

        ParserRuleContext formalParameterListCtx = null;
        for (int i = 0; i < methodDeclaratorCtx.getChildCount(); i++) {
            boolean isRuleContext = methodDeclaratorCtx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) methodDeclaratorCtx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_formalParameterList) {
                formalParameterListCtx = childRuleContext;
            }
        }
        return buildMethodParams(formalParameterListCtx);
    }

    private List<MethodParam> buildMethodParams(ParserRuleContext formalParameterListCtx) {
        List<MethodParam> methodParams = Lists.newArrayList();

        if (formalParameterListCtx == null) {
            return Lists.newArrayList();
        }

        ParserRuleContext formalParametersCtx = null;
        ParserRuleContext lastFormalParameterCtx = null;
        for (int i = 0; i < formalParameterListCtx.getChildCount(); i++) {
            boolean isRuleContext = formalParameterListCtx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) formalParameterListCtx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_formalParameters) {
                formalParametersCtx = childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_lastFormalParameter) {
                lastFormalParameterCtx = childRuleContext;
            }
        }
        List<ParserRuleContext> formalParameterCtxList = Lists.newArrayList();
        if (formalParametersCtx != null) {
            for (int i = 0; i < formalParametersCtx.getChildCount(); i++) {
                boolean isRuleContext = formalParametersCtx.getChild(i) instanceof RuleContext;
                if (!isRuleContext) {
                    continue;
                }
                ParserRuleContext childRuleContext = (ParserRuleContext) formalParametersCtx.getChild(i);
                if (childRuleContext.getRuleIndex() == Java8Parser.RULE_formalParameter) {
                    formalParameterCtxList.add(childRuleContext);
                }
            }
        }

        if (lastFormalParameterCtx != null) {
            if (lastFormalParameterCtx.getChildCount() == 1) {
                formalParameterCtxList.add((ParserRuleContext) lastFormalParameterCtx.getChild(0));
            } else {
                methodParams.add(buildMethodParam(lastFormalParameterCtx));
            }
        }

        for (ParserRuleContext formalParameterCtx : formalParameterCtxList) {
            methodParams.add(buildMethodParam(formalParameterCtx));
        }

        return methodParams.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private MethodParam buildMethodParam(ParserRuleContext parameterCtx) {
        ParserRuleContext typeCtx = null;
        ParserRuleContext variableDeclaratorIdCtx = null;
        for (int i = 0; i < parameterCtx.getChildCount(); i++) {
            boolean isRuleContext = parameterCtx.getChild(i) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) parameterCtx.getChild(i);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                typeCtx = childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorId) {
                variableDeclaratorIdCtx = childRuleContext;
            }
        }
        if (typeCtx != null && variableDeclaratorIdCtx != null) {
            // unannType
            //	:	unannPrimitiveType
            //	|	unannReferenceType
            //	;
            boolean isReferenceType = false;
            ParserRuleContext child = (ParserRuleContext) typeCtx.getChild(0);
            if (child.getRuleIndex() == Java8Parser.RULE_unannReferenceType) {
                isReferenceType = true;
            }
            return MethodParam.builder()
                    .type(typeCtx)
                    .typeText(typeCtx.getText())
                    .isReferenceType(isReferenceType)
                    .variableId(variableDeclaratorIdCtx)
                    .build();
        }
        return null;
    }


}
