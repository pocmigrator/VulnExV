package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Lexer;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@Getter
public class ClassTypeVisitor extends Java8BaseVisitor<RuleNode> {
    private List<String> superClasses = Lists.newArrayList();

    @Override
    public RuleNode visitClassType(Java8Parser.ClassTypeContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode terminalNode = (TerminalNode) child;
                if (terminalNode.getSymbol().getType() == Java8Lexer.Identifier) {
                    superClasses.add(terminalNode.getText());
                }
            }
        }
        return visitChildren(ctx);
    }


}
