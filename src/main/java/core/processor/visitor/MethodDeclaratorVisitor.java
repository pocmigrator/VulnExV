package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.entity.analysis.CallMethod;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.List;
import java.util.Map;

@Getter
public class MethodDeclaratorVisitor extends Java8BaseVisitor<RuleNode> {
    private List<String> types = Lists.newArrayList();
    private List<String> varIds = Lists.newArrayList();

    @Override
    public RuleNode visitUnannType(Java8Parser.UnannTypeContext ctx) {
        types.add(ctx.getText());
        return visitChildren(ctx);
    }

    @Override
    public RuleNode visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        varIds.add(ctx.getText());
        return visitChildren(ctx);
    }

}
