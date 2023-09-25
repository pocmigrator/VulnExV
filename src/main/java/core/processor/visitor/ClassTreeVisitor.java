package core.processor.visitor;

import antlr.Java8BaseVisitor;
import antlr.Java8Lexer;
import antlr.Java8Parser;
import com.google.common.collect.Lists;
import core.entity.analysis.PClass;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.UUID;

@Getter
public class ClassTreeVisitor extends Java8BaseVisitor<RuleNode> {
    private List<PClass> pClassList = Lists.newArrayList();

    /**

     normalInterfaceDeclaration
     :	interfaceModifier* 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody
     ;


     normalClassDeclaration
     :	classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     ;

     *
     * @param ctx the parse tree
     * @return
     */
    @Override
    public RuleNode visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        addPClass(ctx);
        return visitChildren(ctx);
    }


    @Override
    public RuleNode visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        addPClass(ctx);
        return visitChildren(ctx);
    }
    private void addPClass(ParserRuleContext ctx) {
        int startIndex = ctx.getStart().getStartIndex();
        int stopIndex = ctx.getStop().getStopIndex();
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
        ClassTypeVisitor classTypeVisitor = new ClassTypeVisitor();
        classTypeVisitor.visit(ctx);
        String identifierStr = identifier == null ? "" : identifier.getText();
        PClass pClass = PClass.builder()
                .cid(UUID.randomUUID().toString())
                .className(identifierStr)
                .startIndex(startIndex)
                .endIndex(stopIndex)
                .superClassNames(classTypeVisitor.getSuperClasses())
                .tree(ctx)
                .build();

        pClassList.add(pClass);
    }

}
