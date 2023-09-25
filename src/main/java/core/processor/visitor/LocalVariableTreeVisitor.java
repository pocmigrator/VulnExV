package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.List;

@Getter
public class LocalVariableTreeVisitor extends Java8BaseVisitor<RuleNode> {
    private List<String> localVariableDeclarations = Lists.newArrayList();

    @Override
    public RuleNode visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
        ParserRuleContext unannTypeCtx = null;
        ParserRuleContext variableDeclaratorListCtx = null;
        StringBuilder localVariableDeclarationsStr = new StringBuilder();
        for (int j = 0; j < ctx.getChildCount(); j++) {
            boolean isRuleContext = ctx.getChild(j) instanceof RuleContext;
            if (!isRuleContext) {
                continue;
            }
            ParserRuleContext childRuleContext = (ParserRuleContext) ctx.getChild(j);
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_unannType) {
                unannTypeCtx = childRuleContext;
            }
            if (childRuleContext.getRuleIndex() == Java8Parser.RULE_variableDeclaratorList) {
                variableDeclaratorListCtx = childRuleContext;
            }
        }

        if (unannTypeCtx != null) {
            localVariableDeclarationsStr.append(unannTypeCtx.getText()).append(" ");
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
            localVariableDeclarationsStr.append(join);
        }
        localVariableDeclarations.add(localVariableDeclarationsStr.toString());

        return visitChildren(ctx);
    }
}
