package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.List;
import java.util.Set;

@Getter
public class RStatementVarVisitor extends Java8BaseVisitor<RuleNode> {

    private Set<String> varKeyList = Sets.newHashSet();

    @Override
    public RuleNode visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
        varKeyList.add(ctx.getText());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        varKeyList.add(ctx.getText());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitTypeName(Java8Parser.TypeNameContext ctx) {
        varKeyList.add(ctx.getText());
        return visitChildren(ctx);
    }

}
