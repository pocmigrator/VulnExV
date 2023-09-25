package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.Map;

@Getter
public class MethodCallVisitor extends Java8BaseVisitor<RuleNode> {
    private Map<String, String> varTypeMap = Maps.newHashMap();


    @Override
    public RuleNode visitResource(Java8Parser.ResourceContext ctx) {
        fillVarTypeMap(ctx);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitFormalParameter(Java8Parser.FormalParameterContext ctx) {
        fillVarTypeMap(ctx);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
        fillVarTypeMap(ctx);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
        fillVarTypeMap(ctx);

        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        fillVarTypeMap(ctx);

        return visitChildren(ctx);
    }

    private void fillVarTypeMap(ParserRuleContext ctx) {
        ParserRuleContext unannTypeCtx = null;
        ParserRuleContext variableDeclaratorIdCtx = null;
        for (int j = 0; j < ctx.getChildCount(); j++) {
            boolean isRuleContext = ctx.getChild(j) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(j);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                unannTypeCtx = childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorId) {
                variableDeclaratorIdCtx = childRuleContext;
            }
        }

        if (unannTypeCtx != null && variableDeclaratorIdCtx != null) {
            varTypeMap.put(variableDeclaratorIdCtx.getText(), unannTypeCtx.getText());
        }
    }
}
