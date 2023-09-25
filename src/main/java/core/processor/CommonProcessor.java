package core.processor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

public class CommonProcessor {

    public static String formatCodeLine(ParseTree tree) {
        Stack<ParseTree> stack = new Stack<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = tree.getChildCount() - 1; i >= 0; i--) {
            stack.push(tree.getChild(i));
        }

        while (!stack.isEmpty()) {
            ParseTree pop = stack.pop();
            if (pop instanceof TerminalNode) {
                stringBuilder.append(pop.getText()).append(" ");
                continue;
            }

            if (pop.getChildCount() == 0) {
                stringBuilder.append(pop.getText()).append(" ");
                continue;
            }

            for (int i = pop.getChildCount() - 1; i >= 0; i--) {
                stack.push(pop.getChild(i));
            }
        }
        return stringBuilder.toString();
    }
}
