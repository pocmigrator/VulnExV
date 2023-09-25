package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.List;

@Getter
public class CallParamVisitor extends Java8BaseVisitor<RuleNode> {
    private List<ParseTree> expressionList = Lists.newArrayList();
    private List<String> expressionNameList = Lists.newArrayList();

    @Override
    public RuleNode visitExpression(Java8Parser.ExpressionContext ctx) {
        expressionList.add(ctx);
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
        expressionNameList.add(ctx.getText());
        return visitChildren(ctx);
    }
}
