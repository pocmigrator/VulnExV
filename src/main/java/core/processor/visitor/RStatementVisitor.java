package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import core.entity.analysis.CallMethod;
import core.entity.analysis.RStatement;
import core.entity.enums.RStatementTypeEnum;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.List;


@Getter
public class RStatementVisitor extends Java8BaseVisitor<RuleNode> {

    private List<RStatement> rStatementList = Lists.newArrayList();

    @Override
    public RuleNode visitAssignment(Java8Parser.AssignmentContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.assignment.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.localVariableDeclaration.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.methodInvocation.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.methodInvocation_lf_primary.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.methodInvocation_lfno_primary.getCode());
        return visitChildren(ctx);
    }


    @Override
    public RuleNode visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.classInstanceCreationExpression.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.classInstanceCreationExpression_lf_primary.getCode());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        addRStatement(ctx, RStatementTypeEnum.classInstanceCreationExpression_lfno_primary.getCode());
        return visitChildren(ctx);
    }

    private void addRStatement(ParserRuleContext ctx, String type){
        RStatement assignment = RStatement.builder()
                .index(ctx.getStart().getStartIndex())
                .type(type)
                .statementTree(ctx)
                .build();
        rStatementList.add(assignment);
    }

}
